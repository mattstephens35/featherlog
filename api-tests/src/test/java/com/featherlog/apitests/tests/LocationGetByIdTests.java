package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.LocationResponse;
import com.featherlog.apitests.support.BaseApiTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocationGetByIdTests extends BaseApiTest {

    @Test
    public void seededLocationIsReturned() {
        LocationResponse location = LocationClient.getById(1)
                .then().statusCode(200)
                .extract().as(LocationResponse.class);

        Assert.assertEquals(location.id(), Long.valueOf(1));
        Assert.assertEquals(location.name(), "Everglades National Park");
        Assert.assertEquals(location.countryCode(), "us");
    }

    @Test
    public void createdLocationIsReturned() {
        LocationResponse created = LocationClient.createValid();

        LocationResponse fetched = LocationClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(LocationResponse.class);

        Assert.assertEquals(fetched.id(), created.id());
        Assert.assertEquals(fetched.name(), created.name());
        Assert.assertEquals(fetched.country(), created.country());

        LocationClient.delete(created.id());
    }

    @Test
    public void nonExistentIdReturns404() {
        ErrorResponse error = LocationClient.getById(999999)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void nonNumericIdReturns400() {
        ErrorResponse error = LocationClient.getById("abc")
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }
}
