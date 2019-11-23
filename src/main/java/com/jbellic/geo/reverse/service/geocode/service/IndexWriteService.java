package com.jbellic.geo.reverse.service.geocode.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbellic.geo.reverse.service.geocode.domain.GeoLocationData;
import com.jbellic.geo.reverse.service.geocode.utils.IoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@Component
public class IndexWriteService {

    private static final String DELIMETER = "\t";

    @Value("${geo.reverse.service.data.adminCodes1}")
    private String adminCodes1;

    @Value("${geo.reverse.service.data.adminCodes2}")
    private String adminCodes2;

    @Value("${geo.reverse.service.data.cities}")
    private String cities;

    @Value("${geo.reverse.service.data.countryInfo}")
    private String countryInfo;

    @Value("${geo.reverse.service.index.location}")
    private String indexFile;

    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    @Autowired
    public IndexWriteService(ObjectMapper objectMapper, ExecutorService executorService) {
        this.objectMapper = objectMapper;
        this.executorService = executorService;
    }

    /**
     * @noinspection ResultOfMethodCallIgnored
     */
    public void initIndex() throws InterruptedException, IOException {

        List<Callable<Map<String, ArrayList<String>>>> resources = Arrays.asList(
                IoUtils.read(new ClassPathResource(adminCodes1).getFile(), DELIMETER),
                IoUtils.read(new ClassPathResource(adminCodes2).getFile(), DELIMETER),
                IoUtils.read(new ClassPathResource(countryInfo).getFile(), DELIMETER));

        Map<String, ArrayList<String>> cellData = new LinkedHashMap<>();
        executorService.invokeAll(resources).stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }).forEach(cellData::putAll);

        Scanner scanner = new Scanner(new ClassPathResource(cities).getFile());
        List<String> lines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }

        Set<String> cities = new LinkedHashSet<>();
        lines.parallelStream().forEachOrdered(e -> {
            try {
                GeoLocationData geoLocationData = new GeoLocationData(e, cellData);
                cities.add(objectMapper.writeValueAsString(geoLocationData));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        });

        File targetIndex = new File(indexFile);
        targetIndex.getParentFile().mkdirs();
        IoUtils.serialize(cities, targetIndex);
        executorService.shutdown();
    }
}
