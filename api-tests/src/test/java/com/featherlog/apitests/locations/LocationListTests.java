package com.featherlog.apitests.locations;

import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.HabitatType;
import com.featherlog.apitests.model.LocationResponse;
import com.featherlog.apitests.model.PageResponse;
import com.featherlog.apitests.support.BaseApiTest;
import io.restassured.common.mapper.TypeRef;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class LocationListTests extends BaseApiTest {

    @Test
    public void defaultPageReturnsSeedData() {
        PageResponse<LocationResponse> page = LocationClient.list(Map.of())
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<LocationResponse>>() {
                });

        Assert.assertEquals(page.page(), 0);
        Assert.assertEquals(page.size(), 12);
        Assert.assertEquals(page.content().size(), 12);
        Assert.assertTrue(page.totalElements() >= 18);
        Assert.assertTrue(page.first());

        List<String> names = page.content().stream().map(LocationResponse::name).toList();
        for (int i = 0; i < names.size() - 1; i++) {
            Assert.assertTrue(names.get(i).compareToIgnoreCase(names.get(i + 1)) <= 0);
        }
    }

    @Test
    public void secondPageIsNotFirst() {
        PageResponse<LocationResponse> page = LocationClient.list(Map.of("page", 1))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<LocationResponse>>() {
                });

        Assert.assertEquals(page.page(), 1);
        Assert.assertFalse(page.first());
    }

    @Test
    public void customPageSizeIsRespected() {
        PageResponse<LocationResponse> page = LocationClient.list(Map.of("size", 5))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<LocationResponse>>() {
                });

        Assert.assertEquals(page.size(), 5);
        Assert.assertEquals(page.content().size(), 5);
    }

    @Test
    public void sortDescendingOrdersResultsDescending() {
        PageResponse<LocationResponse> page = LocationClient.list(Map.of("sort", "name,desc"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<LocationResponse>>() {
                });

        List<String> names = page.content().stream().map(LocationResponse::name).toList();
        for (int i = 0; i < names.size() - 1; i++) {
            Assert.assertTrue(names.get(i).compareToIgnoreCase(names.get(i + 1)) >= 0);
        }
    }

    @Test
    public void searchFiltersByNameCountryOrRegion() {
        PageResponse<LocationResponse> page = LocationClient.list(Map.of("search", "Park"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<LocationResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (LocationResponse location : page.content()) {
            String haystack = (location.name() + " " + location.country() + " " + location.region()).toLowerCase();
            Assert.assertTrue(haystack.contains("park"));
        }
    }

    @Test
    public void countryFilterReturnsOnlyMatchingCountry() {
        PageResponse<LocationResponse> page = LocationClient.list(Map.of("country", "United States"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<LocationResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (LocationResponse location : page.content()) {
            Assert.assertTrue(location.country().equalsIgnoreCase("United States"));
        }
    }

    @Test
    public void habitatTypeFilterReturnsOnlyMatchingHabitat() {
        PageResponse<LocationResponse> page = LocationClient.list(Map.of("habitatType", "WETLAND"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<LocationResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (LocationResponse location : page.content()) {
            Assert.assertEquals(location.habitatType(), HabitatType.WETLAND);
        }
    }

    @Test
    public void invalidHabitatTypeReturns400() {
        ErrorResponse error = LocationClient.list(Map.of("habitatType", "NOT_A_HABITAT"))
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }

    @Test
    public void invalidPageParamReturns400() {
        ErrorResponse error = LocationClient.list(Map.of("page", "abc"))
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }
}
