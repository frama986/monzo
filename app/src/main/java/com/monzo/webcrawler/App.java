package com.monzo.webcrawler;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class App {

    public static void main(String[] args) {
        new App().start();
    }

    private void start() {
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
            if(input.isBlank()) input = "https://monzo.com/";
            System.out.println(input);

            try {
                CrawlerService crawlerService = CrawlerService.create(input);

                wait(crawlerService);

                System.out.println("COMPLETED - Task completed!");

            } catch (MalformedURLException | URISyntaxException e) {
                System.err.println("ERROR - Invalid URL, please try again...");
            }
        }
    }

    private void wait(CrawlerService crawlerService) {
        try {
            crawlerService.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread Interrupted");
        }
    }
}
