package com.jbellic.geo.reverse.service.geocode.domain;

import com.jbellic.geo.reverse.service.geocode.utils.GeoUtils;
import com.jbellic.geo.reverse.service.geocode.utils.IoUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class GeoLocationData implements Serializable {

    public int geoNameId;
    public String name;
    public double latitude;
    public double longitude;
    public String countryCode;
    public String timeZone;
    public String administrativeSubdivision1;
    public String administrativeSubdivision2;
    public String countryName;
    public String capital;
    public double countryAreaKm;
    public long countryPopulation;
    public String continent;
    public String currencyCode;
    public String currencyName;

    public GeoLocationData(String line, Map<String, ArrayList<String>> adminMap) {
        String[] tokens = line.split("\t");
        this.geoNameId = Integer.valueOf(tokens[0]);
        this.name = tokens[1];
        this.latitude = Double.valueOf(tokens[4]);
        this.longitude = Double.valueOf(tokens[5]);
        this.countryCode = tokens[8];
        this.timeZone = tokens[17];
        String admin1Code = tokens[10];
        String admin2Code = tokens[11];
        this.administrativeSubdivision1 = (String) IoUtils.getOrElse(adminMap.get(String.format("%s.%s", this.countryCode, admin1Code)), 0);
        this.administrativeSubdivision2 = (String) IoUtils.getOrElse(adminMap.get(String.format("%s.%s.%s", this.countryCode, admin1Code, admin2Code)), 0);
        this.countryName = (String) IoUtils.getOrElse(adminMap.get(this.countryCode), 3);
        this.capital = (String) IoUtils.getOrElse(adminMap.get(this.countryCode), 4);
        this.countryAreaKm = Double.parseDouble((String) Objects.requireNonNull(IoUtils.getOrElse(adminMap.get(this.countryCode), 5)));
        this.countryPopulation = Long.parseLong((String) Objects.requireNonNull(IoUtils.getOrElse(adminMap.get(this.countryCode), 6)));
        this.continent = (String) IoUtils.getOrElse(adminMap.get(this.countryCode), 7);
        this.currencyCode = (String) IoUtils.getOrElse(adminMap.get(this.countryCode), 9);
        this.currencyName = (String) IoUtils.getOrElse(adminMap.get(this.countryCode), 10);
    }

    public GeoLocationData(double latitude, double longitude) {
        this.geoNameId = -1;
        this.name = null;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = null;
        this.timeZone = null;
        this.administrativeSubdivision1 = null;
        this.administrativeSubdivision2 = null;
        this.countryName = null;
        this.capital = null;
        this.countryAreaKm = 0;
        this.countryPopulation = 0;
        this.continent = null;
        this.currencyCode = null;
        this.currencyName = null;
    }

    public double distanceTo(GeoLocationData that) {
        return GeoUtils.distanceMeters(this.latitude, that.latitude, this.longitude, that.longitude);
    }
}
