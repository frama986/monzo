package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;

public interface CrawlerEngine {
    void processResult(ParseResult parseResult);

    void await() throws InterruptedException;
}
