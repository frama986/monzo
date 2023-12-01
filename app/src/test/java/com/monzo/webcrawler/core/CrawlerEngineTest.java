package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.utils.Console;
import com.monzo.webcrawler.web.WebParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrawlerEngineTest {

    URI rootUrl = URI.create("https://monzo.com");
    @Mock
    ExecutorService executorService;
    @Mock
    Console console;
    @Mock
    DisplayManager displayManager;

    CrawlerEngine crawlerEngine;

    @Test
    void whenSuccessfulResultIsProcessed_thenValidLinksAreEnqueued() throws MalformedURLException {
        ParseResult parseResult =
                new ParseResult(rootUrl,
                        Arrays.asList(URI.create("https://monzo.com"),
                                URI.create("https://twitter.com/monzo"),
                                URI.create("https://monzo.com/about/"),
                                URI.create("https://monzo.com/blog/"),
                                URI.create("https://web.monzo.com/")));

        crawlerEngine = new CrawlerEngineImpl(rootUrl, executorService, console, displayManager);

        crawlerEngine.processResult(parseResult);

        verify(executorService, times(3)).submit(any(WebParser.class));
        verify(displayManager, times(1)).printResult(parseResult);
    }
}