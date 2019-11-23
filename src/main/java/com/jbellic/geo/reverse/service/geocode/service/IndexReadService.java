package com.jbellic.geo.reverse.service.geocode.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.jbellic.geo.reverse.service.geocode.domain.GeoLocationCellMap;
import com.jbellic.geo.reverse.service.geocode.domain.GeoLocationData;
import com.jbellic.geo.reverse.service.geocode.utils.DistanceComparator;
import com.jbellic.geo.reverse.service.geocode.utils.IoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class IndexReadService {

    private static final int LOW_LEVEL = 7;
    private static final int HIGH_LEVEL = 13;

    @Value("${geo.reverse.service.index.location}")
    private String indexFile;

    private final ObjectMapper objectMapper;
    private final IndexWriteService indexWriteService;
    private final Map<Integer, GeoLocationCellMap> s2MultiLevelIndex = new HashMap<>();

    public IndexReadService(ObjectMapper objectMapper, IndexWriteService indexWriteService) {
        this.objectMapper = objectMapper;
        this.indexWriteService = indexWriteService;
    }

    @PostConstruct
    private void init() throws Exception {
        if (!Files.exists(Paths.get(indexFile))) {
            indexWriteService.initIndex();
        }
        IoUtils.deserialize(indexFile, ArrayList.class)
                .parallelStream().map(e -> {
            try {
                return objectMapper.readValue(e, GeoLocationData.class);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException();
            }
        }).forEachOrdered(this::insertCity);
    }

    private void insertCity(GeoLocationData geoLocationData) {
        for (int level = LOW_LEVEL; level <= HIGH_LEVEL; level++) {
            GeoLocationCellMap geoLocationCellMap = s2MultiLevelIndex.get(level);
            if (geoLocationCellMap == null) {
                geoLocationCellMap = new GeoLocationCellMap();
                s2MultiLevelIndex.put(level, geoLocationCellMap);
            }
            S2CellId cellId = S2CellId.fromLatLng(S2LatLng.fromDegrees(geoLocationData.latitude, geoLocationData.longitude)).parent(level);
            List<S2CellId> neighbours = new ArrayList<>();
            cellId.getAllNeighbors(level, neighbours);
            for (S2CellId neighbour : neighbours) {
                geoLocationCellMap.add(neighbour.toToken(), geoLocationData);
            }
        }
    }

    Set<GeoLocationData> nearestNeighbours(double latitude, double longitude, double maxDistance, int maxHits) {
        GeoLocationData center = new GeoLocationData(latitude, longitude);
        Set<GeoLocationData> hits = new HashSet<>();
        boolean searchedUpperLevelAfterMaxHitsMatched = false;
        for (int level = HIGH_LEVEL; level >= LOW_LEVEL; level--) {
            SortedSet<GeoLocationData> levelMatched = new TreeSet<>(new DistanceComparator(center));
            S2CellId cellId = S2CellId.fromLatLng(S2LatLng.fromDegrees(latitude, longitude)).parent(level);
            List<S2CellId> neighbours = new ArrayList<>();
            cellId.getAllNeighbors(level, neighbours);
            for (S2CellId neighbour : neighbours) {
                Set<GeoLocationData> cities = s2MultiLevelIndex.get(level).map.get(neighbour.toToken());
                if (cities != null) {
                    levelMatched.addAll(cities);
                }
            }
            hits.addAll(levelMatched);
            if (hits.size() >= maxHits) {
                if (!searchedUpperLevelAfterMaxHitsMatched) {
                    searchedUpperLevelAfterMaxHitsMatched = true;
                    continue;
                }
                break;
            }
        }
        hits.removeIf(e -> center.distanceTo(e) > maxDistance);
        return hits.stream().limit(maxHits).collect(Collectors.toSet());
    }
}
