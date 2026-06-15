package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.fixtures.LocationRequestBuilder;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.LocationResponse;
import com.featherlog.apitests.support.BaseApiTest;
import com.featherlog.apitests.support.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LocationUpdateTests extends BaseApiTest {

    @Test
    public void validUpdatePersistsChanges() {
        LocationResponse created = LocationClient.createValid();

        Map<String, Object> updatePayload = LocationRequestBuilder.valid()
                .name("Updated " + TestData.unique())
                .build();

        LocationResponse updated = LocationClient.update(created.id(), updatePayload)
                .then().statusCode(200)
                .extract().as(LocationResponse.class);

        Assert.assertEquals(updated.id(), created.id());
        Assert.assertEquals(updated.name(), updatePayload.get("name"));

        LocationResponse fetched = LocationClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(LocationResponse.class);
        Assert.assertEquals(fetched.name(), updatePayload.get("name"));

        LocationClient.delete(created.id());
    }

    @Test
    public void nonExistentIdReturns404() {
        Map<String, Object> payload = LocationRequestBuilder.valid().build();

        ErrorResponse error = LocationClient.update(999999, payload)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void invalidPayloadReturns400() {
        LocationResponse created = LocationClient.createValid();

        Map<String, Object> payload = LocationRequestBuilder.valid()
                .name(null)
                .build();

        ErrorResponse error = LocationClient.update(created.id(), payload)
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertTrue(error.fieldErrors().containsKey("name"));

        LocationClient.delete(created.id());
    }
}
