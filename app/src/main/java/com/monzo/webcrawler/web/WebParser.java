package com.monzo.webcrawler.web;

import com.monzo.webcrawler.engine.CrawlerEngine;
import com.monzo.webcrawler.models.ParseResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

public class WebParser implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(WebParser.class);
    private final URI targetUrl;
    private final WebClient webClient;
    private final CrawlerEngine crawlerEngine;

    public WebParser(WebClient webClient, URI targetUrl, CrawlerEngine crawlerEngine) {
        this.webClient = webClient;
        this.targetUrl = targetUrl;
        this.crawlerEngine = crawlerEngine;
    }

    @Override
    public void run() {
        log.debug("Parser is running");
        try {
            Document doc = fetchAndParse();
            List<URI> links = extractLinks(doc);
            submitResult(links);
        } catch (Exception e) {
            handleErrors(e);
        }

    }

    private Document fetchAndParse() throws WebClient.WebClientException {
        log.debug("Fetching from {}", targetUrl.toString());
        String response = webClient.get(targetUrl);
        log.trace("Response {}", response);
        return Jsoup.parse(response);
    }

    private List<URI> extractLinks(Document doc) {
        log.debug("Extracting links");
        return doc.select("a[href]")
                .eachAttr("abs:href")
                .stream()
                .map(URI::create)
                .toList();
    }

    private void submitResult(List<URI> links) {
        log.debug("Submitting the result");
        crawlerEngine.processResult(new ParseResult(targetUrl, links));
    }

    private void handleErrors(Exception e) {
        log.error("Error {}", e.getMessage());
        crawlerEngine.processResult(new ParseResult(targetUrl, e.getMessage()));
    }
}
