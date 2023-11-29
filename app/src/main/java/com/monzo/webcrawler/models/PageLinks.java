package com.monzo.webcrawler.models;

import java.net.URI;
import java.util.List;

public record PageLinks(URI url, List<URI> links) { }
