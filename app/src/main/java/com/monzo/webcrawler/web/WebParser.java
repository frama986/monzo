package com.monzo.webcrawler.web;

import com.monzo.webcrawler.engine.CrawlerEngine;
import com.monzo.webcrawler.models.ParseResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.List;

public class WebParser implements Runnable {

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
//        System.out.println("Parser is running");
        try {
            Document doc = fetch();
            List<URI> links = extractLinks(doc);
            submitResult(links);
        } catch (Exception e) {
            handleErrors(e);
        }

    }

    private Document fetch() throws WebClient.WebClientException {
//        System.out.println("Fetching from " + targetUrl.toString());
        String response = webClient.get(targetUrl);
//        System.out.println("Response " + response);
        return Jsoup.parse(response);
//                    .connect(targetUrl.toString()).get();
    }

    private List<URI> extractLinks(Document doc) {
//        System.out.println("Extracting");
        return doc.select("a[href]")
                .eachAttr("abs:href")
                .stream()
                .map(URI::create)
                .toList();
    }

    private void submitResult(List<URI> links) {
//        System.out.println("Submitting");
        crawlerEngine.processResult(new ParseResult(targetUrl, links));
    }

    private void handleErrors(Exception e) {
        System.out.println("Error " + e.getMessage());
        crawlerEngine.processResult(new ParseResult(targetUrl, e.getMessage()));
    }
}
