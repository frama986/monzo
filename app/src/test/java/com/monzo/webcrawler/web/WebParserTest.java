package com.monzo.webcrawler.web;

import com.monzo.webcrawler.core.CrawlerEngine;
import com.monzo.webcrawler.models.ParseResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebParserTest {
    private static final String PAGE_WITH_LINKS = "<!DOCTYPE html><html><head></head><body><a href=\"https://monzo.com\">Monzo</a><a href=\"https://www.google.com\">Google</a></body></html>";
    private static final String PAGE_WITHOUT_LINKS = "<!DOCTYPE html><html><head></head><body></body></html>";

    @Mock
    WebClient webClient;
    @Mock
    URI targetUrl = URI.create("https://monzo.com");
    @Mock
    CrawlerEngine crawlerEngine;
    @Mock
    HttpResponse<String> httpResponse;
    @InjectMocks
    WebParser webParser;

    @Test
    void whenThePageContainsAnchors_thenSubmitTheResultWithListOfLinks() throws WebClient.WebClientException {
        ParseResult parseResult =
                new ParseResult(targetUrl,
                        Arrays.asList(URI.create("https://monzo.com"), URI.create("https://www.google.com")));

        when(webClient.get(targetUrl)).thenReturn(PAGE_WITH_LINKS);

        webParser.run();

        verify(crawlerEngine).processResult(parseResult);
    }

    @Test
    void whenThePageDoesntContainAnchors_thenSubmitTheResultWithNoLinks() throws WebClient.WebClientException {
        ParseResult parseResult = new ParseResult(targetUrl, Collections.emptyList());

        when(webClient.get(targetUrl)).thenReturn(PAGE_WITHOUT_LINKS);

        webParser.run();

        verify(crawlerEngine).processResult(parseResult);
    }

    @Test
    void whenItRaisesAnException_thenSubmitAFailureResult() throws WebClient.WebClientException {
        ParseResult parseResult = new ParseResult(targetUrl, "Status code is 400");
        WebClient.ClientErrorException clientErrorException = Mockito.mock(WebClient.ClientErrorException.class);

        when(clientErrorException.getMessage()).thenReturn("Status code is 400");
        when(webClient.get(targetUrl)).thenThrow(clientErrorException);

        webParser.run();

        verify(crawlerEngine).processResult(parseResult);
    }
}