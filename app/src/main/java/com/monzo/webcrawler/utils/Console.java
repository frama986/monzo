package com.monzo.webcrawler.utils;

import java.util.Scanner;

public class Console {
    private final Scanner scanner;

    private Console() {
        scanner = new Scanner(System.in);
    }

    public static Console instance() {
        return new Console();
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public void println(String format, Object ... args) {
        System.out.printf(format + "%n", args);
    }

    public void println() {
        System.out.printf("%n");
    }
}
