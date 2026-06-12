package com.featherlog.apitests.fixtures;

import com.featherlog.apitests.model.WeatherCondition;
import com.featherlog.apitests.support.TestData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class SightingRequestBuilder {

    private Object birdId;
    private Object locationId;
    private Object sightingDate;
    private Object sightingTime;
    private Object quantity;
    private Object observerName;
    private Object weatherCondition;
    private Object notes;
    private Object favorite;

    public static SightingRequestBuilder valid(long birdId, long locationId) {
        return new SightingRequestBuilder()
                .birdId(birdId)
                .locationId(locationId)
                .sightingDate(LocalDate.now())
                .sightingTime(LocalTime.of(8, 0, 0))
                .quantity(1)
                .observerName(TestData.uniqueName("Test Observer"))
                .weatherCondition(WeatherCondition.SUNNY)
                .notes("Logged during API testing.")
                .favorite(false);
    }

    public SightingRequestBuilder birdId(Object birdId) {
        this.birdId = birdId;
        return this;
    }

    public SightingRequestBuilder locationId(Object locationId) {
        this.locationId = locationId;
        return this;
    }

    public SightingRequestBuilder sightingDate(Object sightingDate) {
        this.sightingDate = sightingDate;
        return this;
    }

    public SightingRequestBuilder sightingTime(Object sightingTime) {
        this.sightingTime = sightingTime;
        return this;
    }

    public SightingRequestBuilder quantity(Object quantity) {
        this.quantity = quantity;
        return this;
    }

    public SightingRequestBuilder observerName(Object observerName) {
        this.observerName = observerName;
        return this;
    }

    public SightingRequestBuilder weatherCondition(Object weatherCondition) {
        this.weatherCondition = weatherCondition;
        return this;
    }

    public SightingRequestBuilder notes(Object notes) {
        this.notes = notes;
        return this;
    }

    public SightingRequestBuilder favorite(Object favorite) {
        this.favorite = favorite;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("birdId", birdId);
        body.put("locationId", locationId);
        body.put("sightingDate", sightingDate);
        body.put("sightingTime", sightingTime);
        body.put("quantity", quantity);
        body.put("observerName", observerName);
        body.put("weatherCondition", weatherCondition);
        body.put("notes", notes);
        body.put("favorite", favorite);
        return body;
    }
}
