package com.jbellic.geo.reverse.service.geocode.utils;

public final class GeoUtils {

    private static final double EARTH_RADIUS = 6371000;

    public static double distanceMeters(double lat1, double lat2, double lon1, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double phiDeltaLat = Math.toRadians(lat2 - lat1);
        double phiDeltaLng = Math.toRadians(lon2 - lon1);
        double a = (Math.sin(phiDeltaLat / 2) * Math.sin(phiDeltaLat / 2)) +
                (Math.cos(phi1) * Math.cos(phi2) * Math.sin(phiDeltaLng / 2) * Math.sin(phiDeltaLng / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
