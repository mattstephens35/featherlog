package com.featherlog.apitests.clients;

import com.featherlog.apitests.fixtures.LocationRequestBuilder;
import com.featherlog.apitests.model.LocationResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class LocationClient {

    private static final String BASE_PATH = "/locations";

    private LocationClient() {
    }

    public static Response list(Map<String, ?> queryParams) {
        return given()
                .queryParams(queryParams)
                .when()
                .get(BASE_PATH);
    }

    public static Response countries() {
        return given()
                .when()
                .get(BASE_PATH + "/countries");
    }

    public static Response getById(Object id) {
        return given()
                .when()
                .get(BASE_PATH + "/{id}", id);
    }

    public static Response create(Object payload) {
        return given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post(BASE_PATH);
    }

    public static Response update(Object id, Object payload) {
        return given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put(BASE_PATH + "/{id}", id);
    }

    public static Response delete(Object id) {
        return given()
                .when()
                .delete(BASE_PATH + "/{id}", id);
    }

    public static LocationResponse createValid() {
        return create(LocationRequestBuilder.valid().build())
                .then()
                .statusCode(201)
                .extract()
                .as(LocationResponse.class);
    }
}
