package com.featherlog.apitests.sightings;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.clients.SightingClient;
import com.featherlog.apitests.fixtures.SightingRequestBuilder;
import com.featherlog.apitests.model.BirdResponse;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.LocationResponse;
import com.featherlog.apitests.model.SightingResponse;
import com.featherlog.apitests.support.BaseApiTest;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class SightingCreateTests extends BaseApiTest {

    private BirdResponse bird;
    private LocationResponse location;

    @BeforeClass
    public void setUp() {
        bird = BirdClient.createValid();
        location = LocationClient.createValid();
    }

    @AfterClass
    public void tearDown() {
        BirdClient.delete(bird.id());
        LocationClient.delete(location.id());
    }

    @Test
    public void validPayloadCreatesAndPersistsSighting() {
        Map<String, Object> payload = SightingRequestBuilder.valid(bird.id(), location.id()).build();

        SightingResponse created = SightingClient.create(payload)
                .then().statusCode(201)
                .extract().as(SightingResponse.class);

        Assert.assertNotNull(created.id());
        Assert.assertEquals(created.bird().id(), bird.id());
        Assert.assertEquals(created.location().id(), location.id());
        Assert.assertEquals(created.observerName(), payload.get("observerName"));

        SightingResponse fetched = SightingClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(SightingResponse.class);
        Assert.assertEquals(fetched.id(), created.id());

        SightingClient.delete(created.id());
    }

    @Test
    public void nonExistentBirdIdReturns404() {
        Map<String, Object> payload = SightingRequestBuilder.valid(999999, location.id()).build();

        ErrorResponse error = SightingClient.create(payload)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void nonExistentLocationIdReturns404() {
        Map<String, Object> payload = SightingRequestBuilder.valid(bird.id(), 999999).build();

        ErrorResponse error = SightingClient.create(payload)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void malformedJsonBodyReturns400() {
        ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body("{ this is not valid json")
                .when().post("/sightings")
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }

    @Test
    public void invalidWeatherConditionEnumReturns400() {
        Map<String, Object> payload = SightingRequestBuilder.valid(bird.id(), location.id())
                .weatherCondition("NOT_A_WEATHER")
                .build();

        ErrorResponse error = SightingClient.create(payload)
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }

    @DataProvider(name = "invalidSightingPayloads")
    public Object[][] invalidSightingPayloads() {
        return new Object[][]{
                {SightingRequestBuilder.valid(bird.id(), location.id()).birdId(null), "birdId"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).locationId(null), "locationId"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).sightingDate(null), "sightingDate"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).sightingDate(LocalDate.now().plusDays(1)), "sightingDate"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).quantity(null), "quantity"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).quantity(0), "quantity"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).quantity(10001), "quantity"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).observerName(null), "observerName"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).observerName("   "), "observerName"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).observerName("x".repeat(101)), "observerName"},
                {SightingRequestBuilder.valid(bird.id(), location.id()).notes("x".repeat(2001)), "notes"},
        };
    }

    @Test(dataProvider = "invalidSightingPayloads")
    public void invalidPayloadReturns400WithFieldError(SightingRequestBuilder builder, String expectedField) {
        ErrorResponse error = SightingClient.create(builder.build())
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
        Assert.assertNotNull(error.fieldErrors());
        Assert.assertTrue(error.fieldErrors().containsKey(expectedField),
                "Expected fieldErrors to contain '" + expectedField + "' but was " + error.fieldErrors());
    }
}
