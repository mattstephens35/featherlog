package com.featherlog.apitests.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class StatsClient {

    private static final String BASE_PATH = "/stats";

    private StatsClient() {
    }

    public static Response summary() {
        return given().when().get(BASE_PATH + "/summary");
    }

    public static Response timeline() {
        return given().when().get(BASE_PATH + "/timeline");
    }

    public static Response conservation() {
        return given().when().get(BASE_PATH + "/conservation");
    }

    public static Response lifers() {
        return given().when().get(BASE_PATH + "/lifers");
    }

    public static Response activity() {
        return given().when().get(BASE_PATH + "/activity");
    }
}
