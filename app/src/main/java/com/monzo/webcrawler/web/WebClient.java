package com.monzo.webcrawler.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebClient {

    private static final WebClient WEB_CLIENT = new WebClient();

    private final HttpClient client;

    private WebClient() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public static WebClient instance() {
        return WEB_CLIENT;
    }

    public String get(String url) throws WebClientException {
        return get(URI.create(url));
    }

    public String get(URI url) throws WebClientException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofMinutes(2))
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
        if(code >= 400 && code <= 499) throw new ClientErrorException(code, response.body());
        if(code >= 500 && code <= 599) throw new ServerErrorException(code, response.body());
        // We should not care about 3xx since it should follow automatically
    }

    public static class WebClientException extends Exception {
        public WebClientException(String message) {
            super(message);
        }
    }

    public static class HttpStatusException extends WebClientException {
        private final int statusCode;

        public HttpStatusException(int statusCode, String statusMessage) {
            super(statusMessage);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    public static class ClientErrorException extends HttpStatusException {

        public ClientErrorException(int statusCode, String statusMessage) {
            super(statusCode, statusMessage);
        }
    }

    public static class ServerErrorException extends HttpStatusException {

        public ServerErrorException(int statusCode, String statusMessage) {
            super(statusCode, statusMessage);
        }
    }
}
