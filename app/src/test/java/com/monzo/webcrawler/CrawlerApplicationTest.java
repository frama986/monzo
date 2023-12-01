package com.monzo.webcrawler;

import com.monzo.webcrawler.core.CrawlerEngine;
import com.monzo.webcrawler.core.CrawlerEngineFactory;
import com.monzo.webcrawler.core.EngineObserver;
import com.monzo.webcrawler.utils.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlerApplicationTest {

    @Mock
    Console console;
    @Mock
    CrawlerEngineFactory crawlerEngineFactory;
    @Mock
    EngineObserver observer;
    @Mock
    CrawlerEngine crawlerEngine;

    CrawlerApplication crawlerApplication;

    @BeforeEach
    public void init() {
        crawlerApplication = new CrawlerApplication(console, crawlerEngineFactory);
    }

    @Test
    void whenInstanceIsInvoked_thenANewInstanceIsReturned() {
        CrawlerApplication result = CrawlerApplication.instance();

        assertNotNull(result);
        assertInstanceOf(CrawlerApplication.class, result);
    }

    @Test
    void givenAnInitialUrl_whenTheApplicationIsStarted_thenItAllocatesAndStartsAnEngineToCrawlTheUrl() throws InterruptedException {
        String url = "https://www.google.com";
        when(console.readLine()).thenReturn(url).thenReturn("exit");
        when(crawlerEngineFactory.engineFrom(URI.create(url))).thenReturn(crawlerEngine);
        when(crawlerEngine.start()).thenReturn(observer);

        crawlerApplication.start();

        verify(console, times(2)).readLine();
        verify(crawlerEngineFactory).engineFrom(URI.create(url));
        verify(observer).awaitTermination();
        verify(console).println(eq("COMPLETED - Task completed in %d seconds"), any());
    }

    @Test
    void givenMalformedUrl_whenStartIsInvoked_thenItRaisesAnError() {
        String url = "malformed url";
        when(console.readLine()).thenReturn(url).thenReturn("exit");

        crawlerApplication.start();

        verify(console, times(2)).readLine();
        verify(console).println("ERROR - Invalid URL, please try again...");
    }

    @Test
    void givenExitAsInput_whenStartIsInvoked_thenItExits() {
        when(console.readLine()).thenReturn("exit");

        crawlerApplication.start();

        verify(console).readLine();
        verify(console).println("Goodbye!");
    }
}