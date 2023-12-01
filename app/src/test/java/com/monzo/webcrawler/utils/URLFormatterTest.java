package com.monzo.webcrawler.utils;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class URLFormatterTest {

    @Test
    void whenValidUrl_thenReturnURI() throws MalformedURLException {
        String url = "https://www.google.com";

        URI result = URLFormatter.parseAndValidateUrl(url);

        assertEquals(url, result.toString());
    }

    @Test
    void whenValidUrlMissingProtocols_thenAddHttpsAndReturnURI() throws MalformedURLException {
        String url = "www.google.com";

        URI result = URLFormatter.parseAndValidateUrl(url);

        assertEquals("https://" + url, result.toString());
    }

    @Test
    void whenInvalidUrl_thenRaiseMalformedURLException() throws MalformedURLException {
        String url = "notavalidurl";

        assertThrows(MalformedURLException.class, () -> URLFormatter.parseAndValidateUrl(url));
    }

    @Test
    void whenAnUrlIsProvided_thenReturnsAValidDomain() {
        URI uri = URI.create("https://www.google.com");

        String result = URLFormatter.getDomainName(uri);

        assertEquals("google.com", result);
    }
}