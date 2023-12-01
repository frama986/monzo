package com.monzo.webcrawler;

import com.monzo.webcrawler.core.CrawlerEngine;
import com.monzo.webcrawler.core.CrawlerEngineFactory;
import com.monzo.webcrawler.core.EngineObserver;
import com.monzo.webcrawler.utils.Console;
import com.monzo.webcrawler.utils.URLFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;

public class CrawlerApplication {

    private static final Logger log = LoggerFactory.getLogger(CrawlerApplication.class);

    private static final String DEFAULT_URL = "https://www.google.com/";

    private final Console console;

    private final CrawlerEngineFactory crawlerEngineFactory;

    CrawlerApplication(Console console, CrawlerEngineFactory crawlerEngineFactory) {
        this.console = console;
        this.crawlerEngineFactory = crawlerEngineFactory;
    }

    public static CrawlerApplication instance() {
        return new CrawlerApplication(Console.instance(), new CrawlerEngineFactory());
    }

    public void start() {
        titleMessage();

        while(true) {
            startMessage();

            String input = console.readLine();
            log.debug(input);

            if("exit".equalsIgnoreCase(input)) {
                goodbyeMessage();
                return;
            }

            try {
                URI uri = formatInput(input);

                Instant startTime = Instant.now();

                CrawlerEngine crawlerEngine = crawlerEngineFactory.engineFrom(uri);
                EngineObserver observer = crawlerEngine.start();

                wait(observer);

                Instant endTime = Instant.now();
                console.println("COMPLETED - Task completed in %d seconds", Duration.between(startTime, endTime).getSeconds());

            } catch (MalformedURLException e) {
                console.println("ERROR - Invalid URL, please try again...");
            }
        }
    }

    private URI formatInput(String input) throws MalformedURLException {
        if(input == null || input.isBlank()) {
            return URI.create(DEFAULT_URL);
        }
        return URLFormatter.parseAndValidateUrl(input);
    }

    private void wait(EngineObserver observer) {
        try {
            observer.awaitTermination();
        } catch (InterruptedException e) {
            log.error("Thread Interrupted");
            Thread.currentThread().interrupt();
        }
    }

    private void goodbyeMessage() {
        console.println();
        console.println("Goodbye!");
        console.println();
    }

    private void startMessage() {
        console.println();
        console.println("START - Type the initial url (or exit to stop): ");
    }

    private void titleMessage() {
        console.println();
        console.println("#######__ WebCrawler 0.1 __#######");
        console.println();
    }
}
