package com.monzo.webcrawler.utils;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URI;

public class URLFormatter {

    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http","https"});

    public static URI parseAndValidateUrl(String strUrl) throws MalformedURLException {
        try {
            if(strUrl == null || strUrl.isBlank()) throw new IllegalArgumentException("URL cannot be empty or blank");

            strUrl = addProtocolIfMissing(strUrl);

            validateUrl(strUrl);

            return new URI(strUrl);
        } catch (Exception e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    public static String getDomainName(URI url) {
        String host = url.getHost();
        return (host != null && host.startsWith("www.")) ? host.substring(4) : host;
    }

    private static String addProtocolIfMissing(String input) {
        if (! input.startsWith("http")) {
            input = "https://" + input;
        }
        return input;
    }

    private static void validateUrl(String url) throws MalformedURLException {
        if(! URL_VALIDATOR.isValid(url)) {
            throw new MalformedURLException("Invalid URL format");
        }
    }
}
