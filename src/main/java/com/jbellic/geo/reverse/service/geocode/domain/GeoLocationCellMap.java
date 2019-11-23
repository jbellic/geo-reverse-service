package com.jbellic.geo.reverse.service.geocode.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeoLocationCellMap {

    public Map<String, Set<GeoLocationData>> map = new HashMap<>();

    public void add(String cell, GeoLocationData geoLocationData) {
        Set<GeoLocationData> cities = map.computeIfAbsent(cell, k -> new HashSet<>());
        cities.add(geoLocationData);
    }
}
