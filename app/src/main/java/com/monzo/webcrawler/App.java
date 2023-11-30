package com.monzo.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

public class App {

    public static void main(String[] args) {
        new App().start();
//        new App().test();
    }

    private void start() {
        System.out.println("WebCrawler0.1");
        System.out.println();

        Console console = System.console();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            System.out.println("Insert the initial url or type exit to stop:");

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

                System.out.println("Task completed");

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
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

    private void test() {
        try {
            Document doc = Jsoup.connect("https://monzo.com/").get();
            List<URI> list = doc.select("a[href]")
                    .eachAttr("abs:href")
                    .stream()
                    .map(URI::create)
                    .toList();
            System.out.println(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
