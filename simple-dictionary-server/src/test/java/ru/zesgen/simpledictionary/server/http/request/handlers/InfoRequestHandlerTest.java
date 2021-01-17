package ru.zesgen.simpledictionary.server.http.request.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.net.HttpURLConnection;

@ExtendWith(MockitoExtension.class)
class InfoRequestHandlerTest {

    static final String INFO_FILE_CONTENT_STUB = "Info file content stub";

    @Mock ClassLoader mockedClassLoader;
    @Mock Request mockedRequest;
    @Mock Response mockedResponse;

    @InjectMocks
    InfoRequestHandler handlerUnderTest;

    @BeforeEach
    void setUp() {
        lenient().when(mockedRequest.getHttpMethod()).thenReturn(InfoRequestHandler.METHOD_TO_HANDLE);
        lenient().when(mockedRequest.getUriPath()).thenReturn(InfoRequestHandler.URI_PATH_TO_HANDLE1);
        lenient().when(mockedClassLoader.getResourceAsStream(any()))
                .thenReturn(new ByteArrayInputStream(INFO_FILE_CONTENT_STUB.getBytes()));
    }

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(handlerUnderTest.getClassLoader()).isSameAs(mockedClassLoader);
        assertThat(handlerUnderTest.getHttpMethodToHandle()).isSameAs(InfoRequestHandler.METHOD_TO_HANDLE);
        assertThat(handlerUnderTest.getUriPathsToHandle()).isSameAs(InfoRequestHandler.PATHS_TO_HANDLE);
    }

    @Test
    void process_shouldReturnTrueAndSetResponseFieldsCorrectly_withSuitableAndGoodRequest() {
        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isTrue();

        verify(mockedClassLoader).getResourceAsStream(InfoRequestHandler.INFO_FILE_PATH);
        verify(mockedResponse).setTextBody(INFO_FILE_CONTENT_STUB);
        verify(mockedResponse).setResponseCode(HttpURLConnection.HTTP_OK);
        verify(mockedResponse).setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_HTML);
    }

    @Test
    void process_shouldReturnFalseAndNotChangeTheResponse_withRequestHasUnsuitableMethod() {
        when(mockedRequest.getHttpMethod()).thenReturn("Nonexistent method");

        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isFalse();

        verifyNoInteractions(mockedResponse);
    }

    @Test
    void process_shouldReturnFalseAndNotChangeTheResponse_withRequestHasUnsuitablePath() {
        when(mockedRequest.getUriPath()).thenReturn("Nonexistent path");

        boolean isSuccessfullyHandled = handlerUnderTest.process(mockedRequest, mockedResponse);

        assertThat(isSuccessfullyHandled).isFalse();

        verifyNoInteractions(mockedResponse);
    }

    @Test
    void process_shouldThrowException_ifThereIsNoInfoFileInTheResources() throws IOException {
        when(mockedClassLoader.getResourceAsStream(any())).thenReturn(null);

        assertThatThrownBy(() -> handlerUnderTest.process(mockedRequest, mockedResponse))
                .isExactlyInstanceOf(RuntimeException.class);

        verify(mockedClassLoader).getResourceAsStream(InfoRequestHandler.INFO_FILE_PATH);
        verifyNoInteractions(mockedResponse);
    }

}