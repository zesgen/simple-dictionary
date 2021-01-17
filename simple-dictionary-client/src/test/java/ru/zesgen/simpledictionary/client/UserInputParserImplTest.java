package ru.zesgen.simpledictionary.client;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserInputParserImplTest {

    UserInputParserImpl parserUnderTest = new UserInputParserImpl();

    private static class UserInputParserTestCase {
        String[] args;
        Request request;
    }

    @ParameterizedTest
    @MethodSource("provideUserInputParserTestCases")
    void parseAsRequestOrNull_shouldReturnCorrectRequestOrNull_withDifferentInput(UserInputParserTestCase testCase) {
        Request actualRequest = parserUnderTest.parseAsRequestOrNull(testCase.args);

        if (testCase.request == null) {
            assertThat(actualRequest).isNull();
        } else {
            assertThat(actualRequest.getUrl()).isEqualTo(testCase.request.getUrl());
            assertThat(actualRequest.getHttpMethod()).isEqualTo(testCase.request.getHttpMethod());
            assertThat(actualRequest.getHttpBody()).isEqualTo(testCase.request.getHttpBody());
        }
    }

    private static List<UserInputParserTestCase> provideUserInputParserTestCases() throws MalformedURLException {
        List<UserInputParserTestCase> testCases = new LinkedList<>();

        UserInputParserTestCase testCase = new UserInputParserTestCase();
        URL url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "add", "word1", "meaning1", "meaning2"};
        testCase.request = new Request(url, "PUT", "meaning1\nmeaning2");
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "delete", "word1", "meaning1", "meaning2"};
        testCase.request = new Request(url, "DELETE", "meaning1\nmeaning2");
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "get", "word1", "meaning1", "meaning2"};
        testCase.request = new Request(url, "GET", "");
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "ADD", "word1", "meaning1", "meaning2"};
        testCase.request = null;
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "DELETE", "word1", "meaning1", "meaning2"};
        testCase.request = null;
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "GET", "word1", "meaning1", "meaning2"};
        testCase.request = null;
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "get"};
        testCase.request = null;
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "add", "word1"};
        testCase.request = null;
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", "80", "delete", "word1"};
        testCase.request = null;
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{"localhost", ":", "add", "word1"};
        testCase.request = null;
        testCases.add(testCase);

        testCase = new UserInputParserTestCase();
        url = new URL("http://localhost:80/dictionary/meanings?for_word=word1");
        testCase.args = new String[]{":", "80", "add", "word1"};
        testCase.request = null;
        testCases.add(testCase);

        return testCases;
    }
}