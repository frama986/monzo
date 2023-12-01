package com.monzo.webcrawler;

import com.monzo.webcrawler.engine.CrawlerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

import static com.monzo.webcrawler.utils.Console.println;
import static com.monzo.webcrawler.utils.Console.readLine;

public class CrawlerApplication {

    private static final Logger log = LoggerFactory.getLogger(CrawlerApplication.class);

    private static final String DEFAULT_URL = "https://www.google.com/";

    public void start() {
        println();
        println("#######__ WebCrawler 0.1 __#######");
        println();

        while(true) {
            println();
            println("START - Type the initial url (or exit to stop): ");

            String input = readLine();

            if("exit".equalsIgnoreCase(input)) {
                println();
                println("Goodbye!");
                println();
                return;
            }

            input = formatInput(input);
            log.debug(input);

            try {
                CrawlerEngine crawlerEngine = CrawlerEngine.create(input);

                wait(crawlerEngine);

                println("COMPLETED - Task completed!");

            } catch (MalformedURLException e) {
                println("ERROR - Invalid URL, please try again...");
            }
        }
    }

    private String formatInput(String input) {
        if(input == null || input.isBlank()) return DEFAULT_URL;

        if (! input.startsWith("http")) {
            input = "https://" + input;
        }
        return input;
    }

    private void wait(CrawlerEngine crawlerEngine) {
        try {
            crawlerEngine.await();
        } catch (InterruptedException e) {
            log.error("Thread Interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
