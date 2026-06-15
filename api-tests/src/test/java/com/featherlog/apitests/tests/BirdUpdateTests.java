package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.fixtures.BirdRequestBuilder;
import com.featherlog.apitests.model.BirdResponse;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.support.BaseApiTest;
import com.featherlog.apitests.support.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class BirdUpdateTests extends BaseApiTest {

    @Test
    public void validUpdatePersistsChanges() {
        BirdResponse created = BirdClient.createValid();

        Map<String, Object> updatePayload = BirdRequestBuilder.valid()
                .scientificName(created.scientificName())
                .commonName("Updated " + TestData.unique())
                .build();

        BirdResponse updated = BirdClient.update(created.id(), updatePayload)
                .then().statusCode(200)
                .extract().as(BirdResponse.class);

        Assert.assertEquals(updated.id(), created.id());
        Assert.assertEquals(updated.commonName(), updatePayload.get("commonName"));

        BirdResponse fetched = BirdClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(BirdResponse.class);
        Assert.assertEquals(fetched.commonName(), updatePayload.get("commonName"));

        BirdClient.delete(created.id());
    }

    @Test
    public void nonExistentIdReturns404() {
        Map<String, Object> payload = BirdRequestBuilder.valid().build();

        ErrorResponse error = BirdClient.update(999999, payload)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void invalidPayloadReturns400() {
        BirdResponse created = BirdClient.createValid();

        Map<String, Object> payload = BirdRequestBuilder.valid()
                .scientificName(created.scientificName())
                .commonName(null)
                .build();

        ErrorResponse error = BirdClient.update(created.id(), payload)
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertTrue(error.fieldErrors().containsKey("commonName"));

        BirdClient.delete(created.id());
    }

    @Test
    public void duplicateScientificNameReturns409() {
        BirdResponse birdA = BirdClient.createValid();
        BirdResponse birdB = BirdClient.createValid();

        Map<String, Object> payload = BirdRequestBuilder.valid()
                .scientificName(birdA.scientificName())
                .build();

        BirdClient.update(birdB.id(), payload)
                .then().statusCode(409);

        BirdClient.delete(birdA.id());
        BirdClient.delete(birdB.id());
    }
}
