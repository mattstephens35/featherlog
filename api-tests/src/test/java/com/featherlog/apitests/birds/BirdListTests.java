package com.featherlog.apitests.birds;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.model.BirdResponse;
import com.featherlog.apitests.model.ConservationStatus;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.PageResponse;
import com.featherlog.apitests.support.BaseApiTest;
import io.restassured.common.mapper.TypeRef;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class BirdListTests extends BaseApiTest {

    @Test
    public void defaultPageReturnsSeedData() {
        PageResponse<BirdResponse> page = BirdClient.list(Map.of())
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<BirdResponse>>() {
                });

        Assert.assertEquals(page.page(), 0);
        Assert.assertEquals(page.size(), 12);
        Assert.assertEquals(page.content().size(), 12);
        Assert.assertTrue(page.totalElements() >= 25);
        Assert.assertTrue(page.first());

        List<String> names = page.content().stream().map(BirdResponse::commonName).toList();
        for (int i = 0; i < names.size() - 1; i++) {
            Assert.assertTrue(names.get(i).compareToIgnoreCase(names.get(i + 1)) <= 0);
        }
    }

    @Test
    public void secondPageIsNotFirst() {
        PageResponse<BirdResponse> page = BirdClient.list(Map.of("page", 1))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<BirdResponse>>() {
                });

        Assert.assertEquals(page.page(), 1);
        Assert.assertFalse(page.first());
    }

    @Test
    public void customPageSizeIsRespected() {
        PageResponse<BirdResponse> page = BirdClient.list(Map.of("size", 5))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<BirdResponse>>() {
                });

        Assert.assertEquals(page.size(), 5);
        Assert.assertEquals(page.content().size(), 5);
    }

    @Test
    public void sortDescendingOrdersResultsDescending() {
        PageResponse<BirdResponse> page = BirdClient.list(Map.of("sort", "commonName,desc"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<BirdResponse>>() {
                });

        List<String> names = page.content().stream().map(BirdResponse::commonName).toList();
        for (int i = 0; i < names.size() - 1; i++) {
            Assert.assertTrue(names.get(i).compareToIgnoreCase(names.get(i + 1)) >= 0);
        }
    }

    @Test
    public void searchFiltersByCommonNameScientificNameOrFamily() {
        PageResponse<BirdResponse> page = BirdClient.list(Map.of("search", "eagle"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<BirdResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (BirdResponse bird : page.content()) {
            String haystack = (bird.commonName() + " " + bird.scientificName() + " " + bird.family()).toLowerCase();
            Assert.assertTrue(haystack.contains("eagle"));
        }
    }

    @Test
    public void familyFilterReturnsOnlyMatchingFamily() {
        PageResponse<BirdResponse> page = BirdClient.list(Map.of("family", "Accipitridae"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<BirdResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (BirdResponse bird : page.content()) {
            Assert.assertEquals(bird.family(), "Accipitridae");
        }
    }

    @Test
    public void conservationStatusFilterReturnsOnlyMatchingStatus() {
        PageResponse<BirdResponse> page = BirdClient.list(Map.of("conservationStatus", "LEAST_CONCERN"))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<BirdResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (BirdResponse bird : page.content()) {
            Assert.assertEquals(bird.conservationStatus(), ConservationStatus.LEAST_CONCERN);
        }
    }

    @Test
    public void invalidConservationStatusReturns400() {
        ErrorResponse error = BirdClient.list(Map.of("conservationStatus", "NOT_A_STATUS"))
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }

    @Test
    public void invalidPageParamReturns400() {
        ErrorResponse error = BirdClient.list(Map.of("page", "abc"))
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }
}
