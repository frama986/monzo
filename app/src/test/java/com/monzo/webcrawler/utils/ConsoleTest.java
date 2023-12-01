package com.monzo.webcrawler.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final String TEST_MESSAGE = "test message";

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

        console.println(TEST_MESSAGE);

        assertEquals(TEST_MESSAGE, outputStreamCaptor.toString().trim());
    }

    @Test
    void whenReadLineIsInvoked_thenItReadsTheInput() {
        provideInput(TEST_MESSAGE);

        Console console = Console.instance();

        String result = console.readLine();

        assertEquals(TEST_MESSAGE, result);
    }

    @Test
    void whenAConsoleIsCreated_thenReturnTheInstance() {


        Console result = Console.instance();
        assertNotNull(result);
        assertInstanceOf(Console.class, result);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
}