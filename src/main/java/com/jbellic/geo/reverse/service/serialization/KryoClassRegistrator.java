package com.jbellic.geo.reverse.service.serialization;

import com.esotericsoftware.kryo.Kryo;

public interface KryoClassRegistrator {

    void register(Kryo kryo);

}
