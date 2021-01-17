package ru.zesgen.simpledictionary.server.utility;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class ExceptionProcessorImplTest {

    @Test
    void extractStackTrace_shouldReturnCorrectString_withComplexException() {
        ExceptionProcessorImpl processorUnderTest = new ExceptionProcessorImpl();
        String actualStackTrace = null;
        try {
            methodWithException();
        } catch (Throwable e) {
            actualStackTrace = processorUnderTest.extractStackTrace(e);
        }
        assertThat(actualStackTrace).isNotNull();
        assertThat(actualStackTrace)
                .contains("IOException")
                .contains("Original exception message")
                .contains("RuntimeException")
                .contains("Second exception message");
    }

    void methodWithException() {
        try {
            throw new IOException("Original exception message");
        } catch (IOException e) {
            throw new RuntimeException("Second exception message", e);
        }
    }


}