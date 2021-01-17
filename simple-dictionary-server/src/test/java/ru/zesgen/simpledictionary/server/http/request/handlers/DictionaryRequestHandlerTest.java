package ru.zesgen.simpledictionary.server.http.request.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zesgen.simpledictionary.server.dao.Dictionary;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.net.HttpURLConnection;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DictionaryRequestHandlerTest {

    static final String HTTP_METHOD_STUB = "HTTP method stub";
    static final String HTTP_URI_PATH_STUB = "HTTP URI path stub";
    public static final List<String> PATHS_TO_HANDLE_STUB = Collections.singletonList(HTTP_URI_PATH_STUB);

    static final String WORD_STUB = "Some word";

    @Mock Dictionary mockedDictionary;
    @Mock Request mockedRequest;
    @Mock Response mockedResponse;

    Map<String, List<String>> httpUriParametersStub = new HashMap<>();

    DictionaryRequestHandler handlerUnderTest;

    @BeforeEach
    void setUp() {
        handlerUnderTest = new DictionaryRequestHandler(HTTP_METHOD_STUB, PATHS_TO_HANDLE_STUB,
                mockedDictionary) {

            @Override
            public boolean process(Request request, Response response) {
                return false;
            }
        };

        lenient().when(mockedRequest.getHttpMethod()).thenReturn(HTTP_METHOD_STUB);
        lenient().when(mockedRequest.getUriPath()).thenReturn(HTTP_URI_PATH_STUB);

        httpUriParametersStub.put(DictionaryRequestHandler.PARAMETER_KEY_OF_WORD_FOR,
                Collections.singletonList(WORD_STUB));
        lenient().when(mockedRequest.getUriParameters()).thenReturn(httpUriParametersStub);
    }

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(handlerUnderTest.getDictionary()).isSameAs(mockedDictionary);
        assertThat(handlerUnderTest.getHttpMethodToHandle()).isSameAs(HTTP_METHOD_STUB);
        assertThat(handlerUnderTest.getUriPathsToHandle()).isSameAs(PATHS_TO_HANDLE_STUB);
        assertThat(handlerUnderTest.getWordFor()).isNull();
    }

    @Test
    void checkRequestAndPrepareResponse_shouldReturnTrueAndPrepareResponseAndWordForCorrectly_withGoodRequest() {
        boolean isSuccessfullyChecked = handlerUnderTest.checkRequestAndPrepareResponse(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyChecked).isTrue();
        assertThat(handlerUnderTest.getWordFor()).isSameAs(WORD_STUB);

        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        verifyNoMoreInteractions(mockedResponse);
    }


    @Test
    void checkRequestAndPrepareResponse_shouldReturnFalseAndPrepareResponseCorrectly_ifRequestUriParametersAreEmpty() {
        httpUriParametersStub.clear();

        boolean isSuccessfullyChecked = handlerUnderTest.checkRequestAndPrepareResponse(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyChecked).isFalse();
        assertThat(handlerUnderTest.getWordFor()).isNull();

        verify(mockedResponse).setTextBody(DictionaryRequestHandler.MESSAGE_IF_BAD_WORD_PARAMETER);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
    }

    @Test
    void checkRequestAndPrepareResponse_shouldReturnFalseAndPrepareResponseCorrectly_ifWordParameterIsNotInTheUri() {
        httpUriParametersStub.remove(DictionaryRequestHandler.PARAMETER_KEY_OF_WORD_FOR);

        boolean isSuccessfullyChecked = handlerUnderTest.checkRequestAndPrepareResponse(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyChecked).isFalse();

        verify(mockedResponse).setTextBody(DictionaryRequestHandler.MESSAGE_IF_BAD_WORD_PARAMETER);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
    }

    @Test
    void checkRequestAndPrepareResponse_shouldReturnFalseAndPrepareResponseCorrectly_ifTwoWordParametersAreInTheUri() {
        List<String> words = Arrays.asList("word1", "word2");
        httpUriParametersStub.put(DictionaryRequestHandler.PARAMETER_KEY_OF_WORD_FOR, words);

        boolean isSuccessfullyChecked = handlerUnderTest.checkRequestAndPrepareResponse(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyChecked).isFalse();

        verify(mockedResponse).setTextBody(DictionaryRequestHandler.MESSAGE_IF_BAD_WORD_PARAMETER);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
    }

    @Test
    void process_shouldReturnTrueAndSetResponseFieldsCorrectly_ifTheUriWordParametersIsEmpty() {
        httpUriParametersStub.put(DictionaryRequestHandler.PARAMETER_KEY_OF_WORD_FOR, Collections.singletonList(""));

        boolean isSuccessfullyChecked = handlerUnderTest.checkRequestAndPrepareResponse(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyChecked).isFalse();

        verify(mockedResponse).setTextBody(DictionaryRequestHandler.MESSAGE_IF_BAD_WORD_PARAMETER);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
    }

}