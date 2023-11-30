package com.monzo.webcrawler.utils;

public class Console {

    public static final java.io.Console console = System.console();

    public static String readLine() {
        return console.readLine();
    }

    public static void println(String format, Object ... args) {
        System.out.printf(format + "%n", args);
    }

    public static void println() {
        System.out.printf("%n");
    }
}
