package com.monzo.webcrawler;

import com.monzo.webcrawler.models.PageLinks;
import com.monzo.webcrawler.web.WebClient;
import com.monzo.webcrawler.web.WebParser;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CrawlerService {
    private static final int CONCURRENCY_LEVEL = 20;
    private final Set<String> visitedLinks;
    private final String domain;
    private final AtomicInteger totalUniqueLinks;
    private final AtomicInteger totalVisitedLinks;
    private final ExecutorService executorService;
    private final WebClient webClient;

    public CrawlerService(String rootUrl, WebClient webClient) throws MalformedURLException {
        this.visitedLinks = new HashSet<>();
        this.totalUniqueLinks = new AtomicInteger();
        this.totalVisitedLinks = new AtomicInteger();
        this.webClient = webClient;

        ThreadFactory factory = Thread.ofVirtual().factory();
        this.executorService = Executors.newFixedThreadPool(CONCURRENCY_LEVEL, factory);

        URI url = URI.create(rootUrl);
        this.domain = url.getHost();

        enqueue(url);
    }

    public static CrawlerService create(String rootUrl) throws MalformedURLException {
        return new CrawlerService(rootUrl, WebClient.instance());
    }

    synchronized public void processResult(PageLinks pageLinks) {
        printResult(pageLinks);

        pageLinks.links().stream()
                .filter(l -> domain.equals(l.getHost()) && !visitedLinks.contains(l.toString()))
                .forEach(this::enqueue);

        totalVisitedLinks.incrementAndGet();
        System.out.println("Links found: " + totalUniqueLinks + " -- Visited: " + totalVisitedLinks);
        System.out.println();

        if(isTerminated()) {
            this.notifyAll();
        }
    }

    public boolean isTerminated() {
        return totalUniqueLinks.intValue() == totalVisitedLinks.intValue();
    }

    private void enqueue(URI url) {
        visitedLinks.add(url.toString());
        totalUniqueLinks.incrementAndGet();

        executorService.submit(new WebParser(webClient, url, this));
    }

    private void printResult(PageLinks pageLinks) {
        System.out.println(" -- " + pageLinks.url().toString());
        pageLinks.links().forEach(l -> System.out.println("   |-- " + l.toString()));
        System.out.println("Total links in the page: " + pageLinks.links().size());
        System.out.println();
    }
}
