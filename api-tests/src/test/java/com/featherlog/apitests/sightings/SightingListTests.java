package com.featherlog.apitests.sightings;

import com.featherlog.apitests.clients.BirdClient;
import com.featherlog.apitests.clients.LocationClient;
import com.featherlog.apitests.clients.SightingClient;
import com.featherlog.apitests.fixtures.SightingRequestBuilder;
import com.featherlog.apitests.model.BirdResponse;
import com.featherlog.apitests.model.ErrorResponse;
import com.featherlog.apitests.model.LocationResponse;
import com.featherlog.apitests.model.PageResponse;
import com.featherlog.apitests.model.SightingResponse;
import com.featherlog.apitests.support.BaseApiTest;
import com.featherlog.apitests.support.TestData;
import io.restassured.common.mapper.TypeRef;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Map;

public class SightingListTests extends BaseApiTest {

    private BirdResponse bird;
    private LocationResponse location;
    private SightingResponse sighting;
    private String searchToken;

    @BeforeClass
    public void setUp() {
        bird = BirdClient.createValid();
        location = LocationClient.createValid();
        searchToken = "ListTok" + TestData.unique();

        sighting = SightingClient.create(SightingRequestBuilder.valid(bird.id(), location.id())
                        .observerName(TestData.uniqueName("List Observer"))
                        .notes("Notes containing " + searchToken)
                        .sightingDate(LocalDate.now())
                        .favorite(true)
                        .build())
                .then().statusCode(201)
                .extract().as(SightingResponse.class);
    }

    @AfterClass
    public void tearDown() {
        SightingClient.delete(sighting.id());
        BirdClient.delete(bird.id());
        LocationClient.delete(location.id());
    }

    @Test
    public void defaultPageReturnsSeedData() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of())
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertEquals(page.page(), 0);
        Assert.assertEquals(page.size(), 10);
        Assert.assertEquals(page.content().size(), 10);
        Assert.assertTrue(page.totalElements() >= 32);

        for (int i = 0; i < page.content().size() - 1; i++) {
            LocalDate current = page.content().get(i).sightingDate();
            LocalDate next = page.content().get(i + 1).sightingDate();
            Assert.assertTrue(!current.isBefore(next));
        }
    }

    @Test
    public void secondPageIsNotFirst() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of("page", 1))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertEquals(page.page(), 1);
        Assert.assertFalse(page.first());
    }

    @Test
    public void customPageSizeIsRespected() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of("size", 5))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertEquals(page.size(), 5);
        Assert.assertEquals(page.content().size(), 5);
    }

    @Test
    public void birdIdFilterReturnsOnlyMatchingBird() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of("birdId", bird.id(), "size", 50))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (SightingResponse s : page.content()) {
            Assert.assertEquals(s.bird().id(), bird.id());
        }
        Assert.assertTrue(page.content().stream().anyMatch(s -> s.id().equals(sighting.id())));
    }

    @Test
    public void locationIdFilterReturnsOnlyMatchingLocation() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of("locationId", location.id(), "size", 50))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (SightingResponse s : page.content()) {
            Assert.assertEquals(s.location().id(), location.id());
        }
        Assert.assertTrue(page.content().stream().anyMatch(s -> s.id().equals(sighting.id())));
    }

    @Test
    public void dateRangeFilterReturnsSightingsWithinRange() {
        String today = LocalDate.now().toString();

        PageResponse<SightingResponse> page = SightingClient.list(Map.of("fromDate", today, "toDate", today, "size", 50))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (SightingResponse s : page.content()) {
            Assert.assertEquals(s.sightingDate(), LocalDate.now());
        }
        Assert.assertTrue(page.content().stream().anyMatch(s -> s.id().equals(sighting.id())));
    }

    @Test
    public void favoriteTrueFilterReturnsOnlyFavorites() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of("favorite", true, "size", 50))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (SightingResponse s : page.content()) {
            Assert.assertTrue(s.favorite());
        }
        Assert.assertTrue(page.content().stream().anyMatch(s -> s.id().equals(sighting.id())));
    }

    @Test
    public void favoriteFalseFilterReturnsOnlyNonFavorites() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of("favorite", false, "size", 50))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (SightingResponse s : page.content()) {
            Assert.assertFalse(s.favorite());
        }
    }

    @Test
    public void searchFiltersByNotesObserverNameBirdOrLocation() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of("search", searchToken))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        Assert.assertTrue(page.content().stream().anyMatch(s -> s.id().equals(sighting.id())));
    }

    @Test
    public void combinedFiltersNarrowResults() {
        PageResponse<SightingResponse> page = SightingClient.list(Map.of(
                        "birdId", bird.id(),
                        "locationId", location.id(),
                        "favorite", true,
                        "size", 50))
                .then().statusCode(200)
                .extract().as(new TypeRef<PageResponse<SightingResponse>>() {
                });

        Assert.assertTrue(page.totalElements() >= 1);
        for (SightingResponse s : page.content()) {
            Assert.assertEquals(s.bird().id(), bird.id());
            Assert.assertEquals(s.location().id(), location.id());
            Assert.assertTrue(s.favorite());
        }
        Assert.assertTrue(page.content().stream().anyMatch(s -> s.id().equals(sighting.id())));
    }

    @Test
    public void invalidDateFormatReturns400() {
        ErrorResponse error = SightingClient.list(Map.of("fromDate", "not-a-date"))
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }

    @Test
    public void invalidFavoriteValueReturns400() {
        ErrorResponse error = SightingClient.list(Map.of("favorite", "not-a-bool"))
                .then().statusCode(400)
                .extract().as(ErrorResponse.class);

        Assert.assertEquals(error.status(), 400);
    }
}
