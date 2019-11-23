package com.jbellic.geo.reverse.service.geocode.utils;

import com.jbellic.geo.reverse.service.geocode.domain.GeoLocationData;

import java.util.Comparator;

public class DistanceComparator implements Comparator<GeoLocationData> {

    private final GeoLocationData center;

    public DistanceComparator(GeoLocationData center) {
        this.center = center;
    }

    public int compare(GeoLocationData geoLocationData1, GeoLocationData geoLocationData2) {
        if (geoLocationData1.geoNameId > 0 && geoLocationData1.geoNameId == geoLocationData2.geoNameId) {
            return 0;
        }
        double distance1 = center.distanceTo(geoLocationData1);
        double distance2 = center.distanceTo(geoLocationData2);
        return Double.compare(distance1, distance2);
    }
}
