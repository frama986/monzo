package com.monzo.webcrawler.web;

import com.monzo.webcrawler.core.CrawlerEngine;
import com.monzo.webcrawler.core.CrawlerEngineImpl;

import java.net.URI;

public class CrawlerEngineFactory {

    public CrawlerEngine engineFrom(URI uri) {
        return CrawlerEngineImpl.create(uri);
    }
}
