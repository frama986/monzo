package com.monzo.webcrawler.web;

import com.monzo.webcrawler.CrawlerService;
import com.monzo.webcrawler.models.PageLinks;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.List;

public class WebParser implements Runnable {

    private final URI targetUrl;
    private final WebClient webClient;
    private final CrawlerService crawlerService;

    public WebParser(WebClient webClient, URI targetUrl, CrawlerService crawlerService) {
        this.webClient = webClient;
        this.targetUrl = targetUrl;
        this.crawlerService = crawlerService;
    }

    @Override
    public void run() {
        try {
            Document doc = fetch();
            List<URI> links = extractLinks(doc);
            submitResult(links);
        } catch (WebClient.WebClientException e) {
            throw new RuntimeException(e);
            // TODO: return an error
        }

    }

    private Document fetch() throws WebClient.WebClientException {
        String response = webClient.get(targetUrl);
        return Jsoup.parse(response);
    }

    private List<URI> extractLinks(Document doc) {
        return doc.select("a[href]")
                .eachAttr("abs:href")
                .stream()
                .map(URI::create)
                .toList();
    }

    private void submitResult(List<URI> links) {
        PageLinks pageLinks = new PageLinks(targetUrl, links);
        crawlerService.processResult(pageLinks);
    }
}
