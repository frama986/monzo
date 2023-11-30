package com.monzo.webcrawler.engine;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.web.WebClient;
import com.monzo.webcrawler.web.WebParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.monzo.webcrawler.utils.Console.println;

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

    CrawlerEngine(String rootUrl, WebClient webClient, ExecutorService executorService) throws MalformedURLException {
        URI url = parseAndValidateUrl(rootUrl);

        this.domain = getDomainName(url);
        this.visitedLinks = new HashSet<>();
        this.totalUniqueLinks = new AtomicInteger();
        this.totalVisitedLinks = new AtomicInteger();
        this.countDownLatch = new CountDownLatch(1);
        this.webClient = webClient;
        this.executorService = executorService;

        enqueue(url);
    }

    public static CrawlerEngine create(String rootUrl) throws MalformedURLException {
        log.debug("Creating CrawlerService with rootUrl {}", rootUrl);

        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();

        return new CrawlerEngine(rootUrl, WebClient.instance(),
                Executors.newFixedThreadPool(CONCURRENCY_LEVEL, factory));
    }

    synchronized public void processResult(ParseResult parseResult) {
        try {
            printResult(parseResult);

            if (parseResult.isSuccess()) {
                parseResult.links().stream()
                        .filter(l -> domain.equals(getDomainName(l)) && !visitedLinks.contains(l.toString()))
                        .forEach(this::enqueue);
            }
        }
        finally {
            totalVisitedLinks.incrementAndGet();

            println("Links found: %d -- Visited: %d", totalUniqueLinks.intValue(), totalVisitedLinks.intValue());
            println();

            if(isTerminated()) {
                countDownLatch.countDown();
                this.notifyAll();
            }
        }
    }

    public void await() throws InterruptedException {
        log.debug("waiting...");
        countDownLatch.await();
        log.debug("process completed...");
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
        println(" -- %s", parseResult.url().toString());
        if(parseResult.isFailure()) {
            println("Error to fetch or parse the page: %s", parseResult.error());
        }
        else {
            parseResult.links().forEach(l -> println("   |-- %s", l.toString()));
            println("Total links in the page: %d", parseResult.links().size());
        }
        println();
    }

    private URI parseAndValidateUrl(String strUrl) throws MalformedURLException {
        try {
            URI url = new URI(strUrl).normalize();
            url.toURL();
            return url;
        } catch (Exception e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    private String getDomainName(URI url) {
        String host = url.getHost();
        return host.startsWith("www.") ? host.substring(4) : host;
    }
}
