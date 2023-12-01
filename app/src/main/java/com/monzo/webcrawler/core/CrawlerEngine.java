package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;

public interface CrawlerEngine {
    EngineObserver start();
    void processResult(ParseResult parseResult);
}
