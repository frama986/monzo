package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.web.WebParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.monzo.webcrawler.utils.URLFormatter.getDomainName;

public class CrawlerEngineImpl implements CrawlerEngine {
    private static final Logger log = LoggerFactory.getLogger(CrawlerEngineImpl.class);

    private static final int CONCURRENCY_LEVEL = 30;
    private final Set<String> linkHistory;
    private final ExecutorService executorService;
    private final DisplayManager displayManager;
    private final EngineObserver engineObserver;
    private final URI rootUri;

    CrawlerEngineImpl(URI rootUri, ExecutorService executorService, DisplayManager displayManager, EngineObserver engineObserver) {
        this.linkHistory = new HashSet<>();
        this.rootUri = rootUri;
        this.engineObserver = engineObserver;
        this.executorService = executorService;
        this.displayManager = displayManager;
    }

    public static CrawlerEngine create(URI rootUri) {
        log.debug("Creating CrawlerService with rootUri {}", rootUri);

        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();

        return new CrawlerEngineImpl(
                rootUri,
                Executors.newFixedThreadPool(CONCURRENCY_LEVEL, factory),
                DisplayManager.instance(),
                EngineObserver.instance());
    }

    public EngineObserver start() {
        enqueue(rootUri);

        return engineObserver;
    }

    @Override
    synchronized public void processResult(ParseResult parseResult) {
        try {
            displayManager.printResult(parseResult);

            if (parseResult.isSuccess()) {
                enqueueValidLinks(parseResult);
            }
        }
        finally {
            engineObserver.incrementProcessedLinks();

            engineObserver.printProcessSummary();

            if(engineObserver.isTerminated()) {
                engineObserver.notifyTermination();
            }
        }
    }

    private void enqueueValidLinks(ParseResult parseResult) {
        String domain = getDomainName(parseResult.url());
        parseResult.links().stream()
                .filter(l -> domain.equals(getDomainName(l)) && !linkHistory.contains(l.toString()))
                .forEach(this::enqueue);
    }

    private void enqueue(URI url) {
        linkHistory.add(url.toString());
        engineObserver.incrementEnqueuedLinks();

        executorService.submit(new WebParser(url, this));
        log.debug("Enqueued url {}", url);
    }
}
