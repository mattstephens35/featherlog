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
import com.featherlog.apitests.support.TestData;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class SightingUpdateTests extends BaseApiTest {

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

    private SightingResponse createSighting() {
        return SightingClient.create(SightingRequestBuilder.valid(bird.id(), location.id()).build())
                .then().statusCode(201)
                .extract().as(SightingResponse.class);
    }

    @Test
    public void validUpdatePersistsChanges() {
        SightingResponse created = createSighting();

        Map<String, Object> updatePayload = SightingRequestBuilder.valid(bird.id(), location.id())
                .observerName(TestData.uniqueName("Updated Observer"))
                .quantity(5)
                .build();

        SightingResponse updated = SightingClient.update(created.id(), updatePayload)
                .then().statusCode(200)
                .extract().as(SightingResponse.class);

        Assert.assertEquals(updated.id(), created.id());
        Assert.assertEquals(updated.observerName(), updatePayload.get("observerName"));
        Assert.assertEquals(updated.quantity(), Integer.valueOf(5));

        SightingResponse fetched = SightingClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(SightingResponse.class);
        Assert.assertEquals(fetched.observerName(), updatePayload.get("observerName"));

        SightingClient.delete(created.id());
    }

    @Test
    public void nonExistentIdReturns404() {
        Map<String, Object> payload = SightingRequestBuilder.valid(bird.id(), location.id()).build();

        ErrorResponse error = SightingClient.update(999999, payload)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void nonExistentBirdIdReturns404() {
        SightingResponse created = createSighting();

        Map<String, Object> payload = SightingRequestBuilder.valid(999999, location.id()).build();

        ErrorResponse error = SightingClient.update(created.id(), payload)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);

        SightingClient.delete(created.id());
    }

    @Test
    public void nonExistentLocationIdReturns404() {
        SightingResponse created = createSighting();

        Map<String, Object> payload = SightingRequestBuilder.valid(bird.id(), 999999).build();

        ErrorResponse error = SightingClient.update(created.id(), payload)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);

        SightingClient.delete(created.id());
    }

    @Test
    public void invalidPayloadReturns400() {
        SightingResponse created = createSighting();

        Map<String, Object> payload = SightingRequestBuilder.valid(bird.id(), location.id())
                .quantity(0)
                .build();

        ErrorResponse error = SightingClient.update(created.id(), payload)
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertTrue(error.fieldErrors().containsKey("quantity"));

        SightingClient.delete(created.id());
    }
}
