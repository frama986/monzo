package com.monzo.webcrawler;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.web.WebClient;
import com.monzo.webcrawler.web.WebParser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
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
    private final CountDownLatch countDownLatch;

    public CrawlerService(String rootUrl, WebClient webClient) throws MalformedURLException, URISyntaxException {
        URI url = parseAndValidateUrl(rootUrl);
        this.visitedLinks = new HashSet<>();
        this.totalUniqueLinks = new AtomicInteger();
        this.totalVisitedLinks = new AtomicInteger();
        this.countDownLatch = new CountDownLatch(1);
        this.webClient = webClient;

        ThreadFactory factory = Thread.ofVirtual().factory();
        this.executorService = Executors.newFixedThreadPool(CONCURRENCY_LEVEL, factory);

        this.domain = url.getHost();

        enqueue(url);
    }

    public static CrawlerService create(String rootUrl) throws MalformedURLException, URISyntaxException {
        System.out.println("Creating CrawlerService");
        return new CrawlerService(rootUrl, WebClient.instance());
    }

    synchronized public void processResult(ParseResult parseResult) {
        printResult(parseResult);

        if(parseResult.isSuccess()) {
            parseResult.links().stream()
                    .filter(l -> domain.equals(l.getHost()) && !visitedLinks.contains(l.toString()))
                    .forEach(this::enqueue);
        }

        totalVisitedLinks.incrementAndGet();
        System.out.println("Links found: " + totalUniqueLinks + " -- Visited: " + totalVisitedLinks);
        System.out.println();

        if(isTerminated()) {
            countDownLatch.countDown();
            this.notifyAll();
        }
    }

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public boolean isTerminated() {
        return totalUniqueLinks.intValue() == totalVisitedLinks.intValue();
    }

    private void enqueue(URI url) {
        visitedLinks.add(url.toString());
        totalUniqueLinks.incrementAndGet();

        executorService.submit(new WebParser(webClient, url, this));
//        System.out.println("Url enqueued " + url);
    }

    private void printResult(ParseResult parseResult) {
        System.out.println(" -- " + parseResult.url().toString());
        if(parseResult.isFailure()) {
            System.out.println("Error to fetch or parse the page: " + parseResult.error());
        }
        else {
            parseResult.links().forEach(l -> System.out.println("   |-- " + l.toString()));
            System.out.println("Total links in the page: " + parseResult.links().size());
        }
        System.out.println();
    }

    private URI parseAndValidateUrl(String strUrl) throws MalformedURLException, URISyntaxException {
        try {
            URI url = new URI(strUrl).normalize();
            url.toURL();
            return url;
        } catch (Exception e) {
            throw new MalformedURLException(e.getMessage());
        }
    }
}
