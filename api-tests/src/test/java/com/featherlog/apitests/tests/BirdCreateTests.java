package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.fixtures.BirdRequestBuilder;
import com.featherlog.apitests.model.BirdResponse;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.support.BaseApiTest;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BirdCreateTests extends BaseApiTest {

    @Test
    public void validPayloadCreatesAndPersistsBird() {
        Map<String, Object> payload = BirdRequestBuilder.valid().build();

        BirdResponse created = BirdClient.create(payload)
                .then().statusCode(201)
                .extract().as(BirdResponse.class);

        Assert.assertNotNull(created.id());
        Assert.assertEquals(created.commonName(), payload.get("commonName"));
        Assert.assertEquals(created.scientificName(), payload.get("scientificName"));

        BirdResponse fetched = BirdClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(BirdResponse.class);
        Assert.assertEquals(fetched.id(), created.id());

        BirdClient.delete(created.id());
    }

    @Test
    public void duplicateScientificNameReturns409() {
        Map<String, Object> first = BirdRequestBuilder.valid().build();
        BirdResponse created = BirdClient.create(first)
                .then().statusCode(201)
                .extract().as(BirdResponse.class);

        Map<String, Object> duplicate = BirdRequestBuilder.valid()
                .scientificName(first.get("scientificName"))
                .build();

        BirdClient.create(duplicate)
                .then().statusCode(409);

        BirdClient.delete(created.id());
    }

    @Test
    public void malformedJsonBodyReturns400() {
        ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body("{ this is not valid json")
                .when().post("/birds")
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }

    @DataProvider(name = "invalidBirdPayloads")
    public Object[][] invalidBirdPayloads() {
        return new Object[][]{
                {BirdRequestBuilder.valid().commonName(null), "commonName"},
                {BirdRequestBuilder.valid().commonName("   "), "commonName"},
                {BirdRequestBuilder.valid().commonName("x".repeat(101)), "commonName"},
                {BirdRequestBuilder.valid().scientificName(null), "scientificName"},
                {BirdRequestBuilder.valid().scientificName("   "), "scientificName"},
                {BirdRequestBuilder.valid().scientificName("x".repeat(121)), "scientificName"},
                {BirdRequestBuilder.valid().family(null), "family"},
                {BirdRequestBuilder.valid().family("   "), "family"},
                {BirdRequestBuilder.valid().family("x".repeat(81)), "family"},
                {BirdRequestBuilder.valid().conservationStatus(null), "conservationStatus"},
                {BirdRequestBuilder.valid().sizeCategory(null), "sizeCategory"},
                {BirdRequestBuilder.valid().averageLengthCm(new BigDecimal("-1")), "averageLengthCm"},
                {BirdRequestBuilder.valid().averageWeightGrams(new BigDecimal("-1")), "averageWeightGrams"},
                {BirdRequestBuilder.valid().icon(null), "icon"},
                {BirdRequestBuilder.valid().icon("   "), "icon"},
                {BirdRequestBuilder.valid().colorHex(null), "colorHex"},
                {BirdRequestBuilder.valid().colorHex("   "), "colorHex"},
                {BirdRequestBuilder.valid().colorHex("not-a-color"), "colorHex"},
        };
    }

    @Test(dataProvider = "invalidBirdPayloads")
    public void invalidPayloadReturns400WithFieldError(BirdRequestBuilder builder, String expectedField) {
        ErrorResponse error = BirdClient.create(builder.build())
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
        Assert.assertNotNull(error.fieldErrors());
        Assert.assertTrue(error.fieldErrors().containsKey(expectedField),
                "Expected fieldErrors to contain '" + expectedField + "' but was " + error.fieldErrors());
    }
}
