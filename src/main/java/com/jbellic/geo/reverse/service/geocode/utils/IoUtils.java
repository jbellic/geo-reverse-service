package com.jbellic.geo.reverse.service.geocode.utils;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jbellic.geo.reverse.service.serialization.DefaultKryoContext;
import com.jbellic.geo.reverse.service.serialization.KryoContext;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class IoUtils {

    private static KryoContext kryoContext = DefaultKryoContext.newKryoContextFactory(kryo -> {
    });

    /** @noinspection unchecked*/
    public static List<String> deserialize(String fileName, Class clazz) {
        try (Input input = new Input(new GZIPInputStream(new FileInputStream(fileName)), 65536)) {
            return (List<String>) kryoContext.deserialze(clazz, IOUtils.toByteArray(input));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void serialize(Object value, File file) {
        try (Output output = new Output(new GZIPOutputStream(new FileOutputStream(file)))) {
            kryoContext.serialze(value);
            kryoContext.writeObject(output, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Callable<Map<String, ArrayList<String>>> read(File file, String delimiter) {
        return () -> {
            Map<String, ArrayList<String>> map = new LinkedHashMap<>();
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.startsWith("#")) {
                        String[] tokens = line.split(Pattern.quote(delimiter));
                        if (tokens.length >= 2) {
                            map.put(tokens[0], new ArrayList<>(Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length))));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            return map;
        };
    }

    public static Object getOrElse(ArrayList<String> values, int index) {
        if (values == null) {
            return null;
        } else {
            return values.get(index);
        }
    }
}

