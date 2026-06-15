package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.support.BaseApiTest;
import io.restassured.common.mapper.TypeRef;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BirdFamiliesTests extends BaseApiTest {

    @Test
    public void returnsDistinctFamiliesIncludingSeeded() {
        List<String> families = BirdClient.families()
                .then().statusCode(200)
                .extract().as(new TypeRef<List<String>>() {
                });

        Assert.assertFalse(families.isEmpty());
        Assert.assertTrue(families.contains("Accipitridae"));

        Set<String> distinct = new HashSet<>(families);
        Assert.assertEquals(distinct.size(), families.size());
    }
}
