package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.fixtures.LocationRequestBuilder;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.LocationResponse;
import com.featherlog.apitests.support.BaseApiTest;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LocationCreateTests extends BaseApiTest {

    @Test
    public void validPayloadCreatesAndPersistsLocation() {
        Map<String, Object> payload = LocationRequestBuilder.valid().build();

        LocationResponse created = LocationClient.create(payload)
                .then().statusCode(201)
                .extract().as(LocationResponse.class);

        Assert.assertNotNull(created.id());
        Assert.assertEquals(created.name(), payload.get("name"));
        Assert.assertEquals(created.country(), payload.get("country"));

        LocationResponse fetched = LocationClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(LocationResponse.class);
        Assert.assertEquals(fetched.id(), created.id());

        LocationClient.delete(created.id());
    }

    @Test
    public void countryCodeIsLowercasedInResponse() {
        Map<String, Object> payload = LocationRequestBuilder.valid()
                .countryCode("TL")
                .build();

        LocationResponse created = LocationClient.create(payload)
                .then().statusCode(201)
                .extract().as(LocationResponse.class);

        Assert.assertEquals(created.countryCode(), "tl");

        LocationClient.delete(created.id());
    }

    @Test
    public void malformedJsonBodyReturns400() {
        ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body("{ this is not valid json")
                .when().post("/locations")
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }

    @DataProvider(name = "invalidLocationPayloads")
    public Object[][] invalidLocationPayloads() {
        return new Object[][]{
                {LocationRequestBuilder.valid().name(null), "name"},
                {LocationRequestBuilder.valid().name("   "), "name"},
                {LocationRequestBuilder.valid().name("x".repeat(151)), "name"},
                {LocationRequestBuilder.valid().country(null), "country"},
                {LocationRequestBuilder.valid().country("   "), "country"},
                {LocationRequestBuilder.valid().country("x".repeat(101)), "country"},
                {LocationRequestBuilder.valid().countryCode(null), "countryCode"},
                {LocationRequestBuilder.valid().countryCode("   "), "countryCode"},
                {LocationRequestBuilder.valid().countryCode("usa"), "countryCode"},
                {LocationRequestBuilder.valid().region("x".repeat(101)), "region"},
                {LocationRequestBuilder.valid().habitatType(null), "habitatType"},
                {LocationRequestBuilder.valid().latitude(null), "latitude"},
                {LocationRequestBuilder.valid().latitude(new BigDecimal("91")), "latitude"},
                {LocationRequestBuilder.valid().latitude(new BigDecimal("-91")), "latitude"},
                {LocationRequestBuilder.valid().longitude(null), "longitude"},
                {LocationRequestBuilder.valid().longitude(new BigDecimal("181")), "longitude"},
                {LocationRequestBuilder.valid().longitude(new BigDecimal("-181")), "longitude"},
                {LocationRequestBuilder.valid().bestSeason("x".repeat(121)), "bestSeason"},
                {LocationRequestBuilder.valid().icon(null), "icon"},
                {LocationRequestBuilder.valid().icon("   "), "icon"},
                {LocationRequestBuilder.valid().icon("x".repeat(11)), "icon"},
        };
    }

    @Test(dataProvider = "invalidLocationPayloads")
    public void invalidPayloadReturns400WithFieldError(LocationRequestBuilder builder, String expectedField) {
        ErrorResponse error = LocationClient.create(builder.build())
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
        Assert.assertNotNull(error.fieldErrors());
        Assert.assertTrue(error.fieldErrors().containsKey(expectedField),
                "Expected fieldErrors to contain '" + expectedField + "' but was " + error.fieldErrors());
    }
}
