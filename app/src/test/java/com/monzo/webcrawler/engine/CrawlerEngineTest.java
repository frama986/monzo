package com.monzo.webcrawler.engine;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.web.WebClient;
import com.monzo.webcrawler.web.WebParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrawlerEngineTest {

    String rootUrl = "https://monzo.com";
    @Mock
    WebClient webClient;
    @Mock
    ExecutorService executorService;
    CrawlerEngine crawlerEngine;

    @Test
    void whenSuccessfulResultIsProcessed_thenValidLinksAreEnqueued() throws MalformedURLException {
        ParseResult parseResult =
                new ParseResult(URI.create(rootUrl),
                        Arrays.asList(URI.create("https://monzo.com"),
                                URI.create("https://twitter.com/monzo"),
                                URI.create("https://monzo.com/about/"),
                                URI.create("https://monzo.com/blog/"),
                                URI.create("https://web.monzo.com/")));

        crawlerEngine = new CrawlerEngine(rootUrl, webClient, executorService);

        crawlerEngine.processResult(parseResult);

        verify(executorService, times(3)).submit(any(WebParser.class));
    }

    @Test
    void whenUrlIsMalformed_thenItThrowsMalformedURLException() throws MalformedURLException {
        assertThrows(MalformedURLException.class,
                () -> new CrawlerEngine("malformed_url", webClient, executorService));
    }
}