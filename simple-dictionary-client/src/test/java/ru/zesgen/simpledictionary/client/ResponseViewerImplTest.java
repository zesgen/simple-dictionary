package ru.zesgen.simpledictionary.client;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.HttpURLConnection;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseViewerImplTest {

    static final String HTTP_BODY_STUB = "Http body stub";

    ResponseViewerImpl viewerUnderTest = new ResponseViewerImpl();

    private static class ResponseViewerTestCase {
        String command;
        Response response;
        String expectedView;
    }

    @ParameterizedTest
    @MethodSource("provideResponseViewerTestCases")
    void parseAsRequestOrNull_shouldReturnCorrectRequestOrNull_withDifferentInput(ResponseViewerTestCase testCase) {
        String actualView = viewerUnderTest.makeView(testCase.command, testCase.response);
        assertThat(actualView).isEqualTo(testCase.expectedView);
    }

    private static List<ResponseViewerTestCase> provideResponseViewerTestCases() {
        List<ResponseViewerTestCase> testCases = new LinkedList<>();

        ResponseViewerTestCase testCase = new ResponseViewerTestCase();
        testCase.command = "any";
        testCase.response = new Response(false, 0, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_CONNECTION_FAILED;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "add";
        testCase.response = new Response(true, HttpURLConnection.HTTP_OK, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_ADDITION_IS_SUCCESSFUL;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "get";
        testCase.response = new Response(true, HttpURLConnection.HTTP_OK, HTTP_BODY_STUB);
        testCase.expectedView = HTTP_BODY_STUB;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "delete";
        testCase.response = new Response(true, HttpURLConnection.HTTP_OK, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_DELETING_IS_SUCCESSFUL;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "any other";
        testCase.response = new Response(true, HttpURLConnection.HTTP_OK, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_APPLICATION_ERROR;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "add";
        testCase.response = new Response(true, HttpURLConnection.HTTP_NOT_FOUND, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_ADDITION_FAILED + HTTP_BODY_STUB;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "get";
        testCase.response = new Response(true, HttpURLConnection.HTTP_NOT_FOUND, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_GETTING_FAILED;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "delete";
        testCase.response = new Response(true, HttpURLConnection.HTTP_NOT_FOUND, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_DELETING_FAILED;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "any other";
        testCase.response = new Response(true, HttpURLConnection.HTTP_NOT_FOUND, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_APPLICATION_ERROR;
        testCases.add(testCase);

        testCase = new ResponseViewerTestCase();
        testCase.command = "add";
        testCase.response = new Response(true, HttpURLConnection.HTTP_BAD_REQUEST, HTTP_BODY_STUB);
        testCase.expectedView = ResponseViewerImpl.MESSAGE_IF_BAD_SERVER_RESPONSE + HttpURLConnection.HTTP_BAD_REQUEST +
                "." + ResponseViewerImpl.NEWLINE + ResponseViewerImpl.SERVER_RESPONSE_TITLE + HTTP_BODY_STUB;
        testCases.add(testCase);


        return testCases;
    }

}