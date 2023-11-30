package com.monzo.webcrawler.engine;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.web.WebClient;
import com.monzo.webcrawler.web.WebParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class CrawlerEngine {
    private static final Logger log = LoggerFactory.getLogger(CrawlerEngine.class);
    private static final int CONCURRENCY_LEVEL = 20;
    private final Set<String> visitedLinks;
    private final String domain;
    private final AtomicInteger totalUniqueLinks;
    private final AtomicInteger totalVisitedLinks;
    private final ExecutorService executorService;
    private final WebClient webClient;
    private final CountDownLatch countDownLatch;

    public CrawlerEngine(String rootUrl, WebClient webClient) throws MalformedURLException, URISyntaxException {
        URI url = parseAndValidateUrl(rootUrl);
        this.visitedLinks = new HashSet<>();
        this.totalUniqueLinks = new AtomicInteger();
        this.totalVisitedLinks = new AtomicInteger();
        this.countDownLatch = new CountDownLatch(1);
        this.webClient = webClient;

        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();
        this.executorService = Executors.newFixedThreadPool(CONCURRENCY_LEVEL, factory);

        this.domain = url.getHost();

        enqueue(url);
    }

    public static CrawlerEngine create(String rootUrl) throws MalformedURLException, URISyntaxException {
        log.debug("Creating CrawlerService with rootUrl {}", rootUrl);
        return new CrawlerEngine(rootUrl, WebClient.instance());
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
        log.debug("waiting...");
        countDownLatch.await();
        log.debug("process completed...");
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
        log.debug("Enqueued url {}", url);
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
