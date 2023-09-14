package com.odeyalo.sonata.playlists.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * Tests for utility asserts methods
 */
class AssertsTest {

    @Test
    void validUrlExpectNothingToBeThrown() {
        String validUrl = "https://cdn.sonataproject.com/i/mikunakanoimage";
        assertThatCode(() -> Asserts.validUrl(validUrl)).doesNotThrowAnyException();
    }

    @Test
    void validMalformedUrlAndExpectException() {
        String invalidUrl = "malformed";
        assertThatThrownBy(() -> Asserts.validUrl(invalidUrl))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(String.format("Url [%s] is malformed!", invalidUrl));
    }

    @Test
    void positiveAndExpectNothingToBeThrown() {
        assertThatCode(() -> Asserts.positive(1)).doesNotThrowAnyException();
    }

    @Test
    void testPositiveWithNegativeAndExpectException() {
        assertThatThrownBy(() -> Asserts.positive(-1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }

    @Test
    void testPositiveWithZeroAndExpectException() {
        assertThatThrownBy(() -> Asserts.positive(0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }

    @Test
    void testNegativeWithNegativeAndExpectNothingToBeThrown() {
        assertThatCode(() -> Asserts.negative(-1)).doesNotThrowAnyException();
    }

    @Test
    void negativeWithPositiveExpectException() {
        assertThatThrownBy(() -> Asserts.negative(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected negative number");
    }
    @Test
    void negativeWithZeroExpectException() {
        assertThatThrownBy(() -> Asserts.negative(0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected negative number");
    }

    @Test
    void testZeroAndExpectNothingToBeThrow() {
        assertThatCode(() -> Asserts.zero(0)).doesNotThrowAnyException();
    }

    @Test
    void zeroWithNegativeExpectException() {
        assertThatThrownBy(() -> Asserts.zero(-1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected zero as argument");
    }
    @Test
    void zeroWithPositiveExpectException() {
        assertThatThrownBy(() -> Asserts.zero(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected zero as argument");
    }

    @Test
    void positiveOrNullWithNullExpectNothing() {
        assertThatCode(() -> Asserts.positiveOrNull(null)).doesNotThrowAnyException();
    }
    @Test
    void positiveOrNullWithPositiveExpectNothing() {
        assertThatCode(() -> Asserts.positiveOrNull(1)).doesNotThrowAnyException();
    }

    @Test
    void positiveOrNullWithNegativeExpectException() {
        assertThatCode(() -> Asserts.positiveOrNull(-1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }
    @Test
    void positiveOrNullWithZeroExpectException() {
        assertThatCode(() -> Asserts.positiveOrNull(0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected positive number");
    }

    @Test
    void negativeOrNullWithNullExpectNothing() {
        assertThatCode(() -> Asserts.negativeOrNull(null)).doesNotThrowAnyException();
    }
    @Test
    void negativeOrNullWithNegativeExpectNothing() {
        assertThatCode(() -> Asserts.negativeOrNull(-1)).doesNotThrowAnyException();
    }

    @Test
    void negativeOrNullWithPositiveExpectException() {
        assertThatCode(() -> Asserts.negativeOrNull(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected negative number");
    }

    @Test
    void negativeOrNullWithZeroExpectException() {
        assertThatCode(() -> Asserts.negativeOrNull(0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected negative number");
    }

    @Test
    void zeroOrNullWithNullExpectNothing() {
        assertThatCode(() -> Asserts.zeroOrNull(null)).doesNotThrowAnyException();
    }
    @Test
    void zeroOrNullWithZeroExpectNothing() {
        assertThatCode(() -> Asserts.zeroOrNull(0)).doesNotThrowAnyException();
    }

    @Test
    void zeroOrNullWithPositiveExpectException() {
        assertThatCode(() -> Asserts.zeroOrNull(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected zero as argument");
    }

    @Test
    void zeroOrNullWithNegativeExpectException() {
        assertThatCode(() -> Asserts.zeroOrNull(-1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Expected zero as argument");
    }
}