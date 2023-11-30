package com.monzo.webcrawler;

import com.monzo.webcrawler.engine.CrawlerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class CrawlerApplication {

    private static final Logger log = LoggerFactory.getLogger(CrawlerApplication.class);

    private static final String DEFAULT_URL = "https://www.google.com/";

    public void start() {
        System.out.println("WebCrawler0.1");
        System.out.println();

        Console console = System.console();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            System.out.println();
            System.out.println("START - Type the initial url (or exit to stop): ");

            String input = console.readLine();

            if("exit".equalsIgnoreCase(input)) {
                System.out.println("Goodbye!");
                return;
            }
            if(input.isBlank()) input = DEFAULT_URL;
            log.debug(input);

            try {
                CrawlerEngine crawlerEngine = CrawlerEngine.create(input);

                wait(crawlerEngine);

                System.out.println("COMPLETED - Task completed!");

            } catch (MalformedURLException | URISyntaxException e) {
                System.err.println("ERROR - Invalid URL, please try again...");
            }
        }
    }

    private void wait(CrawlerEngine crawlerEngine) {
        try {
            crawlerEngine.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread Interrupted");
        }
    }
}
