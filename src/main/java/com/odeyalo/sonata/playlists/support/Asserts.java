package com.odeyalo.sonata.playlists.support;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility methods for assertions
 */
public final class Asserts {

    public static void validUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Url [%s] is malformed!", url), e);
        }
    }

    public static void positive(Integer num) {
        if (num <= 0) {
            throw new IllegalArgumentException("Expected positive number");
        }
    }

    public static void negative(Integer num) {
        if (num >= 0) {
            throw new IllegalArgumentException("Expected negative number");
        }
    }
    public static void zero(Integer num) {
        if (num != 0) {
            throw new IllegalArgumentException("Expected zero as argument");
        }
    }
}
