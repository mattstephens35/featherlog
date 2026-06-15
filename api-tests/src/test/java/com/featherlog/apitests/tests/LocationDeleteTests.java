package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.clients.SightingClient;
import com.featherlog.apitests.fixtures.SightingRequestBuilder;
import com.featherlog.apitests.model.BirdResponse;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.LocationResponse;
import com.featherlog.apitests.model.SightingResponse;
import com.featherlog.apitests.support.BaseApiTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocationDeleteTests extends BaseApiTest {

    @Test
    public void existingLocationIsDeleted() {
        LocationResponse created = LocationClient.createValid();

        LocationClient.delete(created.id())
                .then().statusCode(204);

        LocationClient.getById(created.id())
                .then().statusCode(404);
    }

    @Test
    public void nonExistentIdReturns404() {
        ErrorResponse error = LocationClient.delete(999999)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void locationReferencedBySightingReturns409() {
        BirdResponse bird = BirdClient.createValid();
        LocationResponse location = LocationClient.createValid();

        SightingResponse sighting = SightingClient.create(
                        SightingRequestBuilder.valid(bird.id(), location.id()).build())
                .then().statusCode(201)
                .extract().as(SightingResponse.class);

        LocationClient.delete(location.id())
                .then().statusCode(409);

        SightingClient.delete(sighting.id());
        BirdClient.delete(bird.id());
        LocationClient.delete(location.id());
    }
}
