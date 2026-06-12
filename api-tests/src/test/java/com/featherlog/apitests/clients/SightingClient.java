package com.featherlog.apitests.clients;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class SightingClient {

    private static final String BASE_PATH = "/sightings";

    private SightingClient() {
    }

    public static Response list(Map<String, ?> queryParams) {
        return given()
                .queryParams(queryParams)
                .when()
                .get(BASE_PATH);
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

    public static Response toggleFavorite(Object id) {
        return given()
                .when()
                .patch(BASE_PATH + "/{id}/favorite", id);
    }

    public static Response delete(Object id) {
        return given()
                .when()
                .delete(BASE_PATH + "/{id}", id);
    }
}
