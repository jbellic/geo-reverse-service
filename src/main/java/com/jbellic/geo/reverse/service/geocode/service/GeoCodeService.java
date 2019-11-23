package com.jbellic.geo.reverse.service.geocode.service;

import com.jbellic.geo.reverse.service.geocode.domain.GeoLocationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GeoCodeService {

    @Value("${geo.reverse.service.defaults.maxDistanceMeters}")
    private double maxDistanceMeters;

    @Value("${geo.reverse.service.defaults.maxHits}")
    private int maxHits;

    private final IndexReadService indexReadService;

    @Autowired
    public GeoCodeService(IndexReadService indexReadService) {
        this.indexReadService = indexReadService;
    }

    public Set<GeoLocationData> findAll(double latitude, double longitude, double maxDistance, int maxHits) {
        if (maxHits > this.maxHits) {
            maxHits = this.maxHits;
        }
        if (maxDistance < 1) {
            maxDistance = this.maxDistanceMeters;
        }
        return indexReadService.nearestNeighbours(latitude, longitude, maxDistance, maxHits);
    }
}
