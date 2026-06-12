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
import org.testng.Assert;
import org.testng.annotations.Test;

public class SightingFavoriteTests extends BaseApiTest {

    @Test
    public void toggleFlipsFavoriteFlag() {
        BirdResponse bird = BirdClient.createValid();
        LocationResponse location = LocationClient.createValid();

        SightingResponse created = SightingClient.create(SightingRequestBuilder.valid(bird.id(), location.id())
                        .favorite(false)
                        .build())
                .then().statusCode(201)
                .extract().as(SightingResponse.class);
        Assert.assertFalse(created.favorite());

        SightingResponse toggledOn = SightingClient.toggleFavorite(created.id())
                .then().statusCode(200)
                .extract().as(SightingResponse.class);
        Assert.assertTrue(toggledOn.favorite());

        SightingResponse toggledOff = SightingClient.toggleFavorite(created.id())
                .then().statusCode(200)
                .extract().as(SightingResponse.class);
        Assert.assertFalse(toggledOff.favorite());

        SightingClient.delete(created.id());
        BirdClient.delete(bird.id());
        LocationClient.delete(location.id());
    }

    @Test
    public void nonExistentIdReturns404() {
        ErrorResponse error = SightingClient.toggleFavorite(999999)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }
}
