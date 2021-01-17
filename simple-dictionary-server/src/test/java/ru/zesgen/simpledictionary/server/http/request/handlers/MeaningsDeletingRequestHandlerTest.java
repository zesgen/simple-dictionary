package ru.zesgen.simpledictionary.server.http.request.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zesgen.simpledictionary.server.dao.Dictionary;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.net.HttpURLConnection;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class MeaningsDeletingRequestHandlerTest {

    static final String WORD_STUB = "Some word";
    static final String MEANING1_STUB = "Some meaning 1";
    static final String MEANING2_STUB = "Some meaning 2";

    @Mock Dictionary mockedDictionary;
    @Mock Request mockedRequest;
    @Mock Response mockedResponse;

    Map<String, List<String>> httpUriParametersStub = new HashMap<>();
    List<String> httpBodyLinesStub = new LinkedList<>();
    Set<String> meaningsStub = new LinkedHashSet<>();

    @InjectMocks
    MeaningsDeletingRequestHandler handlerUnderTest;

    @BeforeEach
    void setUp() {
        lenient().when(mockedRequest.getHttpMethod()).thenReturn(MeaningsDeletingRequestHandler.METHOD_TO_HANDLE);
        lenient().when(mockedRequest.getUriPath()).thenReturn(MeaningsDeletingRequestHandler.URI_PATH_TO_HANDLE);

        httpUriParametersStub.put(DictionaryRequestHandler.PARAMETER_KEY_OF_WORD_FOR,
                Collections.singletonList(WORD_STUB));
        lenient().when(mockedRequest.getUriParameters()).thenReturn(httpUriParametersStub);
        lenient().when(mockedRequest.getBodyTextLines()).thenReturn(httpBodyLinesStub);
        lenient().when(mockedDictionary.deleteMeanings(any(), any())).thenReturn(true);

        httpBodyLinesStub.add(MEANING1_STUB);
        httpBodyLinesStub.add(MEANING2_STUB);

        meaningsStub.add(MEANING1_STUB);
        meaningsStub.add(MEANING2_STUB);
    }

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(handlerUnderTest.getDictionary()).isSameAs(mockedDictionary);
        assertThat(handlerUnderTest.getHttpMethodToHandle()).isSameAs(MeaningsDeletingRequestHandler.METHOD_TO_HANDLE);
        assertThat(handlerUnderTest.getUriPathsToHandle()).isSameAs(MeaningsDeletingRequestHandler.PATHS_TO_HANDLE);
    }

    @Test
    void process_shouldReturnTrueAndSetResponseFieldsCorrectly_withSuitableAndGoodRequest() {
        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isTrue();

        verify(mockedResponse).setTextBody(MeaningsDeletingRequestHandler.MESSAGE_IF_DELETING_IS_SUCCESSFUL);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_OK);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        verifyNoMoreInteractions(mockedResponse);
        verify(mockedDictionary).deleteMeanings(WORD_STUB, meaningsStub);
        verifyNoMoreInteractions(mockedDictionary);
    }

    @Test
    void process_shouldReturnFalseAndNotChangeTheResponse_withRequestHasUnsuitableMethod() {
        when(mockedRequest.getHttpMethod()).thenReturn("Nonexistent method");

        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isFalse();

        verifyNoInteractions(mockedResponse);
        verifyNoInteractions(mockedDictionary);
    }

    @Test
    void process_shouldReturnFalseAndNotChangeTheResponse_withRequestHasUnsuitablePath() {
        when(mockedRequest.getUriPath()).thenReturn("Nonexistent path");

        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isFalse();

        verifyNoInteractions(mockedResponse);
        verifyNoInteractions(mockedDictionary);
    }

    @Test
    void process_shouldReturnTrueAndSetResponseFieldsCorrectly_withSuitableRequestButEmptyUriParameters() {
        httpUriParametersStub.clear();

        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isTrue();

        verify(mockedResponse).setTextBody(DictionaryRequestHandler.MESSAGE_IF_BAD_WORD_PARAMETER);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        verifyNoMoreInteractions(mockedResponse);
        verifyNoInteractions(mockedDictionary);
    }

    @Test
    void process_shouldReturnTrueAndSetResponseFieldsCorrectly_ifNoMeaningsWereProvided() {
        httpBodyLinesStub.clear();

        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isTrue();

        verify(mockedResponse).setTextBody(MeaningsDeletingRequestHandler.MESSAGE_IF_NO_MEANING_PROVIDED);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        verifyNoMoreInteractions(mockedResponse);
        verifyNoInteractions(mockedDictionary);
    }

    @Test
    void process_shouldReturnTrueAndSetResponseFieldsCorrectly_ifDeletingFailed() {
        when(mockedDictionary.deleteMeanings(any(), any())).thenReturn(false);

        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isTrue();

        verify(mockedResponse).setTextBody(MeaningsDeletingRequestHandler.MESSAGE_IF_DELETING_FAILED);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        verifyNoMoreInteractions(mockedResponse);
        verify(mockedDictionary).deleteMeanings(WORD_STUB, meaningsStub);
        verifyNoMoreInteractions(mockedDictionary);
    }
}