package com.featherlog.apitests.fixtures;

import com.featherlog.apitests.model.HabitatType;
import com.featherlog.apitests.support.TestData;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocationRequestBuilder {

    private Object name;
    private Object country;
    private Object countryCode;
    private Object region;
    private Object habitatType;
    private Object latitude;
    private Object longitude;
    private Object bestSeason;
    private Object description;
    private Object icon;

    public static LocationRequestBuilder valid() {
        String unique = TestData.unique();
        return new LocationRequestBuilder()
                .name("Test Location " + unique)
                .country("Testland")
                .countryCode("tl")
                .region("Test Region")
                .habitatType(HabitatType.FOREST)
                .latitude(new BigDecimal("10.000000"))
                .longitude(new BigDecimal("20.000000"))
                .bestSeason("Year-round")
                .description("A location created for API testing.")
                .icon("🌲");
    }

    public LocationRequestBuilder name(Object name) {
        this.name = name;
        return this;
    }

    public LocationRequestBuilder country(Object country) {
        this.country = country;
        return this;
    }

    public LocationRequestBuilder countryCode(Object countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public LocationRequestBuilder region(Object region) {
        this.region = region;
        return this;
    }

    public LocationRequestBuilder habitatType(Object habitatType) {
        this.habitatType = habitatType;
        return this;
    }

    public LocationRequestBuilder latitude(Object latitude) {
        this.latitude = latitude;
        return this;
    }

    public LocationRequestBuilder longitude(Object longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocationRequestBuilder bestSeason(Object bestSeason) {
        this.bestSeason = bestSeason;
        return this;
    }

    public LocationRequestBuilder description(Object description) {
        this.description = description;
        return this;
    }

    public LocationRequestBuilder icon(Object icon) {
        this.icon = icon;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("country", country);
        body.put("countryCode", countryCode);
        body.put("region", region);
        body.put("habitatType", habitatType);
        body.put("latitude", latitude);
        body.put("longitude", longitude);
        body.put("bestSeason", bestSeason);
        body.put("description", description);
        body.put("icon", icon);
        return body;
    }
}
