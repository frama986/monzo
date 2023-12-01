package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.utils.Console;

public class DisplayManager {

    private final Console console;

    DisplayManager(Console console) {
        this.console = console;
    }

    public static DisplayManager instance() {
        return new DisplayManager(Console.instance());
    }

    public void printResult(ParseResult parseResult) {
        console.println(" -- %s", parseResult.url().toString());
        if(parseResult.isFailure()) {
            console.println("Error to fetch or parse the page: %s", parseResult.error());
        }
        else {
            parseResult.links().forEach(l -> console.println("    |-- %s", l.toString()));
            console.println("Total links in the page: %d", parseResult.links().size());
        }
        console.println();
    }
}
