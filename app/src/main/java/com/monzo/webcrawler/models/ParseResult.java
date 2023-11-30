package com.monzo.webcrawler.models;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public record ParseResult(URI url, List<URI> links, String error) {
    public ParseResult(URI url, List<URI> links) {
        this(url, links, null);
    }

    public ParseResult(URI url, String error) {
        this(url, Collections.emptyList(), error);
    }

    public boolean isSuccess() {
        return !isFailure();
    }

    public boolean isFailure() {
        return error != null && ! error.isBlank();
    }
}
