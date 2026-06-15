package com.featherlog.apitests.tests;

import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.model.CountryOption;
import com.featherlog.apitests.support.BaseApiTest;
import io.restassured.common.mapper.TypeRef;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationCountriesTests extends BaseApiTest {

    @Test
    public void returnsDistinctCountriesIncludingSeeded() {
        List<CountryOption> countries = LocationClient.countries()
                .then().statusCode(200)
                .extract().as(new TypeRef<List<CountryOption>>() {
                });

        Assert.assertFalse(countries.isEmpty());
        Assert.assertTrue(countries.stream()
                .anyMatch(c -> c.country().equals("United States") && c.countryCode().equals("us")));

        Set<CountryOption> distinct = new HashSet<>(countries);
        Assert.assertEquals(distinct.size(), countries.size());
    }
}
