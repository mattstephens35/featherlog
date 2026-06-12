package com.featherlog.apitests.fixtures;

import com.featherlog.apitests.model.ConservationStatus;
import com.featherlog.apitests.model.SizeCategory;
import com.featherlog.apitests.support.TestData;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class BirdRequestBuilder {

    private Object commonName;
    private Object scientificName;
    private Object family;
    private Object conservationStatus;
    private Object sizeCategory;
    private Object averageLengthCm;
    private Object averageWeightGrams;
    private Object habitat;
    private Object diet;
    private Object migratory;
    private Object description;
    private Object icon;
    private Object colorHex;

    public static BirdRequestBuilder valid() {
        String unique = TestData.unique();
        return new BirdRequestBuilder()
                .commonName("Test Bird " + unique)
                .scientificName("Testus birdus " + unique)
                .family("Testidae")
                .conservationStatus(ConservationStatus.LEAST_CONCERN)
                .sizeCategory(SizeCategory.MEDIUM)
                .averageLengthCm(new BigDecimal("30.0"))
                .averageWeightGrams(new BigDecimal("250.0"))
                .habitat("Open woodland and forest edges")
                .diet("Insects and seeds")
                .migratory(true)
                .description("A bird created for API testing.")
                .icon("🐦")
                .colorHex("#336699");
    }

    public BirdRequestBuilder commonName(Object commonName) {
        this.commonName = commonName;
        return this;
    }

    public BirdRequestBuilder scientificName(Object scientificName) {
        this.scientificName = scientificName;
        return this;
    }

    public BirdRequestBuilder family(Object family) {
        this.family = family;
        return this;
    }

    public BirdRequestBuilder conservationStatus(Object conservationStatus) {
        this.conservationStatus = conservationStatus;
        return this;
    }

    public BirdRequestBuilder sizeCategory(Object sizeCategory) {
        this.sizeCategory = sizeCategory;
        return this;
    }

    public BirdRequestBuilder averageLengthCm(Object averageLengthCm) {
        this.averageLengthCm = averageLengthCm;
        return this;
    }

    public BirdRequestBuilder averageWeightGrams(Object averageWeightGrams) {
        this.averageWeightGrams = averageWeightGrams;
        return this;
    }

    public BirdRequestBuilder habitat(Object habitat) {
        this.habitat = habitat;
        return this;
    }

    public BirdRequestBuilder diet(Object diet) {
        this.diet = diet;
        return this;
    }

    public BirdRequestBuilder migratory(Object migratory) {
        this.migratory = migratory;
        return this;
    }

    public BirdRequestBuilder description(Object description) {
        this.description = description;
        return this;
    }

    public BirdRequestBuilder icon(Object icon) {
        this.icon = icon;
        return this;
    }

    public BirdRequestBuilder colorHex(Object colorHex) {
        this.colorHex = colorHex;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("commonName", commonName);
        body.put("scientificName", scientificName);
        body.put("family", family);
        body.put("conservationStatus", conservationStatus);
        body.put("sizeCategory", sizeCategory);
        body.put("averageLengthCm", averageLengthCm);
        body.put("averageWeightGrams", averageWeightGrams);
        body.put("habitat", habitat);
        body.put("diet", diet);
        body.put("migratory", migratory);
        body.put("description", description);
        body.put("icon", icon);
        body.put("colorHex", colorHex);
        return body;
    }
}
