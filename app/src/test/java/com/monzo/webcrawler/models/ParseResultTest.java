package com.monzo.webcrawler.models;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParseResultTest {

    private static final URI TEST_URL = URI.create("https://www.test.com");

    @Test
    void whenThereAreNoErrors_thenIsSuccess() {
        assertTrue(new ParseResult(TEST_URL, List.of(TEST_URL, TEST_URL)).isSuccess());
    }

    @Test
    void whenThereAreErrors_thenIsFailure() {
        assertTrue(new ParseResult(TEST_URL, "generic error").isFailure());
    }
}