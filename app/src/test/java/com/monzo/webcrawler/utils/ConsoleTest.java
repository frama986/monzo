package com.monzo.webcrawler.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void whenInvokePrintln_thenTheMessageIsPrintedOnTheStandardOutput() {
        Console console = Console.instance();

        console.println("Test message");

        assertEquals("Test message", outputStreamCaptor.toString().trim());
    }

    @Test
    void whenAConsoleIsCreated_thenReturnTheInstance() {

        Console result = Console.instance();

        assertNotNull(result);
        assertInstanceOf(Console.class, result);
    }
}