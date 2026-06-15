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

import java.time.LocalDate;

public class SightingGetByIdTests extends BaseApiTest {

    @Test
    public void seededSightingIsReturned() {
        SightingResponse sighting = SightingClient.getById(1)
                .then().statusCode(200)
                .extract().as(SightingResponse.class);

        Assert.assertEquals(sighting.id(), Long.valueOf(1));
        Assert.assertEquals(sighting.bird().id(), Long.valueOf(1));
        Assert.assertEquals(sighting.bird().commonName(), "Bald Eagle");
        Assert.assertEquals(sighting.location().id(), Long.valueOf(2));
        Assert.assertEquals(sighting.sightingDate(), LocalDate.of(2026, 6, 9));
        Assert.assertEquals(sighting.observerName(), "Jordan Avery");
        Assert.assertTrue(sighting.favorite());
    }

    @Test
    public void createdSightingIsReturnedWithNestedSummaries() {
        BirdResponse bird = BirdClient.createValid();
        LocationResponse location = LocationClient.createValid();

        SightingResponse created = SightingClient.create(SightingRequestBuilder.valid(bird.id(), location.id()).build())
                .then().statusCode(201)
                .extract().as(SightingResponse.class);

        SightingResponse fetched = SightingClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(SightingResponse.class);

        Assert.assertEquals(fetched.id(), created.id());
        Assert.assertEquals(fetched.bird().id(), bird.id());
        Assert.assertEquals(fetched.bird().commonName(), bird.commonName());
        Assert.assertEquals(fetched.bird().scientificName(), bird.scientificName());
        Assert.assertEquals(fetched.bird().icon(), bird.icon());
        Assert.assertEquals(fetched.bird().colorHex(), bird.colorHex());

        Assert.assertEquals(fetched.location().id(), location.id());
        Assert.assertEquals(fetched.location().name(), location.name());
        Assert.assertEquals(fetched.location().country(), location.country());
        Assert.assertEquals(fetched.location().countryCode(), location.countryCode());
        Assert.assertEquals(fetched.location().icon(), location.icon());

        SightingClient.delete(created.id());
        BirdClient.delete(bird.id());
        LocationClient.delete(location.id());
    }

    @Test
    public void nonExistentIdReturns404() {
        ErrorResponse error = SightingClient.getById(999999)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void nonNumericIdReturns400() {
        ErrorResponse error = SightingClient.getById("abc")
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }
}
