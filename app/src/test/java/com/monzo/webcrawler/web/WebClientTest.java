package com.monzo.webcrawler.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebClientTest {

    @Mock
    HttpClient httpClient;
    @Mock
    HttpResponse<String> httpResponse;

    @InjectMocks
    WebClient webClient;

    @Test
    void whenUrlIsValid_thenReturnsHtml() throws IOException, InterruptedException, WebClient.WebClientException {
        String htmlPage = "<!DOCTYPE html><html><head></head><body></body></html>";
        URI url = URI.create("https://ww.google.com");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofMinutes(2))
                .build();

        when(httpClient.send(request, HttpResponse.BodyHandlers.ofString()))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(htmlPage);

        String result = webClient.get(url);

        assertEquals(htmlPage, result);
    }

    @Test
    void whenServerReturnA4xxError_thenRaiseAClientErrorException() throws IOException, InterruptedException {
        URI url = URI.create("https://ww.google.com");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofMinutes(2))
                .build();

        when(httpClient.send(request, HttpResponse.BodyHandlers.ofString()))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.uri()).thenReturn(url);

        assertThrows(WebClient.ClientErrorException.class, () -> webClient.get(url));
    }

    @Test
    void whenServerReturnA5xxError_thenRaiseAServerErrorException() throws IOException, InterruptedException {
        URI url = URI.create("https://ww.google.com");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofMinutes(2))
                .build();

        when(httpClient.send(request, HttpResponse.BodyHandlers.ofString()))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.uri()).thenReturn(url);

        assertThrows(WebClient.ServerErrorException.class, () -> webClient.get(url));
    }
}