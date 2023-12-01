package com.monzo.webcrawler.utils;

import java.net.MalformedURLException;
import java.net.URI;

public class URLFormatter {

    public static URI parseAndValidateUrl(String strUrl) throws MalformedURLException {
        try {
            if(strUrl == null || strUrl.isBlank()) throw new IllegalArgumentException();

            strUrl = addProtocolIfMissing(strUrl);

            URI uri = new URI(strUrl).normalize();
            uri.toURL();
            return uri;
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
}
