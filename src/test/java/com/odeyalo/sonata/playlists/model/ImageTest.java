package com.odeyalo.sonata.playlists.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ImageTest {

    @Test
    void urlOnlyWithValidUrlExpectNothingToBeThrown() {
        assertThatCode(() -> Image.urlOnly("https://cdn.sonata.com/i/mikuu")).doesNotThrowAnyException();
    }

    @Test
    void urlOnlyWithInvalidUrlExpectExceptionToBeThrown() {
        String invalidUrl = "invalid";

        assertThatThrownBy(() -> Image.urlOnly(invalidUrl))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(String.format("Url [%s] is malformed!", invalidUrl));
    }

    @Test
    void ofWithValidValuesExpectNothingToBeThrown() {
        assertThatCode(() -> Image.of("https://cdn.sonata.com/i/mikuu", 1, 1)).doesNotThrowAnyException();
    }

    @Test
    void ofWithInvalidUrlExpectException() {
        String invalidUrl = "invalid";

        assertThatThrownBy(() -> Image.of(invalidUrl, 1, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(String.format("Url [%s] is malformed!", invalidUrl));
    }


    @Test
    void ofWithInvalidWidthExpectException() {

        assertThatThrownBy(() -> Image.of("https://cdn.sonata.com/i/mikuu", -1, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }


    @Test
    void ofWithInvalidHeightExpectException() {

        assertThatThrownBy(() -> Image.of("https://cdn.sonata.com/i/mikuu", 1, -1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }

    @Test
    void ofWithZeroHeightExpectException() {

        assertThatThrownBy(() -> Image.of("https://cdn.sonata.com/i/mikuu", 1, 0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }


    @Test
    void ofWithZeroWidthExpectException() {

        assertThatThrownBy(() -> Image.of("https://cdn.sonata.com/i/mikuu", 0, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }

    @Test
    void ofWithNullWidthExpectNothing() {
        assertThatCode(() -> Image.of("https://cdn.sonata.com/i/mikuu", null, 1)).doesNotThrowAnyException();
    }

    @Test
    void ofWithNullHeightExpectNothing() {
        assertThatCode(() -> Image.of("https://cdn.sonata.com/i/mikuu", 1, null)).doesNotThrowAnyException();
    }
}