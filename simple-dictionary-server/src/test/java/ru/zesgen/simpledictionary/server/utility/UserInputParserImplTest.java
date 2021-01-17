package ru.zesgen.simpledictionary.server.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

class UserInputParserImplTest {

    UserInputParserImpl parserUnderTest = new UserInputParserImpl();

    @Test
    void parseStringAsTcpPort_shouldReturnCorrectPort_withCorrectString() {
        OptionalInt expectedPort = OptionalInt.of(12345);
        String userInput = " \t12345\r\n";
        OptionalInt actualPort = parserUnderTest.parseStringAsTcpPort(userInput);
        assertThat(actualPort).isEqualTo(expectedPort);
    }

    @Test
    void parseStringAsTcpPort_shouldReturnEmptyOptionalInt_withStringContainingLettersBeforeDigits() {
        OptionalInt expectedPort = OptionalInt.empty();
        String userInput = " \tdf123\r\n";
        OptionalInt actualPort = parserUnderTest.parseStringAsTcpPort(userInput);
        assertThat(actualPort).isEqualTo(expectedPort);
    }

    @Test
    void parseStringAsTcpPort_shouldReturnEmptyOptionalInt_withStringContainingLettersAfterDigits() {
        OptionalInt expectedPort = OptionalInt.empty();
        String userInput = " \t123df\r\n";
        OptionalInt actualPort = parserUnderTest.parseStringAsTcpPort(userInput);
        assertThat(actualPort).isEqualTo(expectedPort);
    }

    @Test
    void parseStringAsTcpPort_shouldReturnEmptyOptionalInt_withStringContainingZeroIntValue() {
        OptionalInt expectedPort = OptionalInt.empty();
        String userInput = "0";
        OptionalInt actualPort = parserUnderTest.parseStringAsTcpPort(userInput);
        assertThat(actualPort).isEqualTo(expectedPort);
    }

    @Test
    void parseStringAsTcpPort_shouldReturnEmptyOptionalInt_withStringContainingIntValueGreaterThanAllowed() {
        OptionalInt expectedPort = OptionalInt.empty();
        String userInput = "65536";
        OptionalInt actualPort = parserUnderTest.parseStringAsTcpPort(userInput);
        assertThat(actualPort).isEqualTo(expectedPort);
    }
}