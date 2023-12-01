package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.web.WebParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlerEngineImplTest {
    URI rootUrl = URI.create("https://monzo.com");
    @Mock
    ExecutorService executorService;
    @Mock
    DisplayManager displayManager;
    @Mock
    EngineObserver engineObserver;

    CrawlerEngineImpl crawlerEngine;

    @BeforeEach
    public void init() {
        crawlerEngine = new CrawlerEngineImpl(rootUrl, executorService, displayManager, engineObserver);
    }

    @Test
    void whenSuccessfulResultIsProcessed_thenValidLinksAreEnqueued() throws MalformedURLException {
        ParseResult parseResult = generateResult();


        crawlerEngine.processResult(parseResult);

        verify(executorService, times(3)).submit(any(WebParser.class));
        verify(displayManager, times(1)).printResult(parseResult);
        verify(engineObserver, never()).notifyTermination();
        verify(engineObserver).incrementProcessedLinks();
        verify(engineObserver, times(3)).incrementEnqueuedLinks();
    }

    @Test
    void whenTheProcessIsTerminated_thenTheTerminationIsNotified() {
        ParseResult parseResult = generateResult();

        when(engineObserver.isTerminated()).thenReturn(true);

        crawlerEngine.processResult(parseResult);

        verify(engineObserver).notifyTermination();
    }

    @Test
    void whenStartIsInvoked_thenTheRootUrlIsEnqueued() {
        crawlerEngine.start();

        verify(executorService).submit(any(WebParser.class));
    }

    @Test
    void whenStartIsInvoked_thenTheObserverIsReturned() {
        EngineObserver observer = crawlerEngine.start();

        assertNotNull(observer);
    }

    private ParseResult generateResult() {
        return  new ParseResult(rootUrl,
                        Arrays.asList(URI.create("https://monzo.com"),
                                URI.create("https://twitter.com/monzo"),
                                URI.create("https://monzo.com/about/"),
                                URI.create("https://monzo.com/blog/"),
                                URI.create("https://web.monzo.com/")));
    }
}