package com.monzo.webcrawler.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebClient {

    private static final Logger log = LoggerFactory.getLogger(WebClient.class);

    private static final WebClient WEB_CLIENT = new WebClient();

    private final HttpClient client;

    WebClient() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build());
    }

    WebClient(HttpClient httpClient) {
        this.client = httpClient;
    }

    public static WebClient instance() {
        return WEB_CLIENT;
    }

    public String get(URI url) throws WebClientException {
        log.debug("get url {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = send(request);
        return response.body();
    }

    private HttpResponse<String> send(HttpRequest request) throws WebClientException {
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new WebClientException(e.getMessage());
        }
        checkResponseStatus(response);
        return response;
    }

    private void checkResponseStatus(HttpResponse<String> response) throws HttpStatusException {
        int code = response.statusCode();
        if(code >= 400 && code <= 499) throw new ClientErrorException(response);
        if(code >= 500 && code <= 599) throw new ServerErrorException(response);
        // We should not care about 3xx since it should follow automatically
    }

    public static class WebClientException extends Exception {
        public WebClientException(String message) {
            super(message);
        }
    }

    public static class HttpStatusException extends WebClientException {
        private final int statusCode;

        private final String url;

        public HttpStatusException(int statusCode, String url) {
            super("Status code is " + statusCode);
            this.statusCode = statusCode;
            this.url = url;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class ClientErrorException extends HttpStatusException {

        public ClientErrorException(HttpResponse<String> response) {
            super(response.statusCode(), response.uri().toString());
        }
    }

    public static class ServerErrorException extends HttpStatusException {

        public ServerErrorException(HttpResponse<String> response) {
            super(response.statusCode(), response.uri().toString());
        }
    }
}
