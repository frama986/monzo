package com.monzo.webcrawler.utils;

public class Console {

    public final java.io.Console console;

    private Console() {
        console = System.console();
    }

    public static Console instance() {
        return new Console();
    }

    public String readLine() {
        return console.readLine();
    }

    public void println(String format, Object ... args) {
        System.out.printf(format + "%n", args);
    }

    public void println() {
        System.out.printf("%n");
    }
}
