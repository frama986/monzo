package com.monzo.webcrawler.core;

import com.monzo.webcrawler.models.ParseResult;
import com.monzo.webcrawler.utils.Console;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DisplayManagerTest {

    private static final URI TEST_URL = URI.create("https://www.test.com");
    private static final ParseResult SUCCESSFUL_RESULT = new ParseResult(TEST_URL, List.of(TEST_URL, TEST_URL));
    private static final ParseResult FAILURE_RESULT = new ParseResult(TEST_URL, "generic error");
    @Mock
    Console console;
    @InjectMocks
    DisplayManager displayManager;

    @Test
    void givenSuccessResult_whenPrintResultIsCalled_thenPrintTheListOfLinks() {
        displayManager.printResult(SUCCESSFUL_RESULT);

        verify(console).println(" -- %s", "https://www.test.com");
        verify(console, times(2)).println("    |-- %s", "https://www.test.com");
        verify(console).println("Total links in the page: %d", 2);
    }

    @Test
    void givenFailureResult_whenPrintResultIsCalled_thenPrintTheError() {
        displayManager.printResult(FAILURE_RESULT);

        verify(console).println(" -- %s", "https://www.test.com");
        verify(console).println("Error to fetch or parse the page: %s", "generic error");
    }
}