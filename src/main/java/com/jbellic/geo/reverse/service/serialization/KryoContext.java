package com.jbellic.geo.reverse.service.serialization;

import com.esotericsoftware.kryo.io.Output;

public interface KryoContext {

    byte[] serialze(Object obj);

    byte[] serialze(Object obj, int bufferSize);

    Object deserialze(Class clazz, byte[] serialized);

    void writeObject(Output output, Object object);

}
