package com.jbellic.geo.reverse.service.geocode.controller;

import com.jbellic.geo.reverse.service.geocode.domain.GeoLocationData;
import com.jbellic.geo.reverse.service.geocode.service.GeoCodeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Log4j2
@RestController
@RequestMapping("/api/geocode")
public class GeoCodeController {

    private final GeoCodeService geoCodeService;

    public GeoCodeController(GeoCodeService geoCodeService) {
        this.geoCodeService = geoCodeService;
    }

    @GetMapping("")
    ResponseEntity<Set<GeoLocationData>> reverseGeoCode(@RequestParam double latitude, @RequestParam double longitude,
                                                        @RequestParam double maxDistance, @RequestParam int maxHits) {
        log.info("reverse geocoding {} {}", latitude, longitude);
        Set<GeoLocationData> cities = geoCodeService.findAll(latitude, longitude, maxDistance, maxHits);
        log.info("found {} matches: {}", cities.size(), cities.toString());
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

}
