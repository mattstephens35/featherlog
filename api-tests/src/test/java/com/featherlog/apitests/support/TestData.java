package com.featherlog.apitests.support;

import java.util.UUID;

public final class TestData {

    private TestData() {
    }

    public static String unique() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String uniqueName(String prefix) {
        return prefix + " " + unique();
    }
}
