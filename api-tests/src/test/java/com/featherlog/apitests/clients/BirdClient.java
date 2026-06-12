package com.featherlog.apitests.clients;

import com.featherlog.apitests.fixtures.BirdRequestBuilder;
import com.featherlog.apitests.model.BirdResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class BirdClient {

    private static final String BASE_PATH = "/birds";

    private BirdClient() {
    }

    public static Response list(Map<String, ?> queryParams) {
        return given()
                .queryParams(queryParams)
                .when()
                .get(BASE_PATH);
    }

    public static Response families() {
        return given()
                .when()
                .get(BASE_PATH + "/families");
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

    public static BirdResponse createValid() {
        return create(BirdRequestBuilder.valid().build())
                .then()
                .statusCode(201)
                .extract()
                .as(BirdResponse.class);
    }
}
