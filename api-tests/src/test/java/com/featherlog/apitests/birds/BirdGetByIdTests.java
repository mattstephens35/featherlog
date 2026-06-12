package com.featherlog.apitests.birds;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.model.BirdResponse;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.support.BaseApiTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BirdGetByIdTests extends BaseApiTest {

    @Test
    public void seededBirdIsReturned() {
        BirdResponse bird = BirdClient.getById(1)
                .then().statusCode(200)
                .extract().as(BirdResponse.class);

        Assert.assertEquals(bird.id(), Long.valueOf(1));
        Assert.assertEquals(bird.commonName(), "Bald Eagle");
    }

    @Test
    public void createdBirdIsReturned() {
        BirdResponse created = BirdClient.createValid();

        BirdResponse fetched = BirdClient.getById(created.id())
                .then().statusCode(200)
                .extract().as(BirdResponse.class);

        Assert.assertEquals(fetched.id(), created.id());
        Assert.assertEquals(fetched.commonName(), created.commonName());
        Assert.assertEquals(fetched.scientificName(), created.scientificName());

        BirdClient.delete(created.id());
    }

    @Test
    public void nonExistentIdReturns404() {
        ErrorResponse error = BirdClient.getById(999999)
                .then().statusCode(404)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 404);
    }

    @Test
    public void nonNumericIdReturns400() {
        ErrorResponse error = BirdClient.getById("abc")
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }
}
