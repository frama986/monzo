package com.monzo.webcrawler.core;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CrawlerEngineFactoryTest {

    private final URI targetUrl = URI.create("https://monzo.com");

    CrawlerEngineFactory crawlerEngineFactory = new CrawlerEngineFactory();

    @Test
    void whenEngineFromIsInvoked_thenANewFactoryInstanceIsReturned() {
        CrawlerEngine result = crawlerEngineFactory.engineFrom(targetUrl);

        assertNotNull(result);
        assertInstanceOf(CrawlerEngineImpl.class, result);
    }

}