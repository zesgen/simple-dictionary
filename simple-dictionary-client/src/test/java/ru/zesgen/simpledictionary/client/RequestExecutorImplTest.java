package ru.zesgen.simpledictionary.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestExecutorImplTest {

    static final String HTTP_REQUEST_BODY_STUB = "Request body stub";
    static final String HTTP_RESPONSE_BODY_NORMAL_STUB = "Response body normal stub";
    static final String HTTP_RESPONSE_BODY_ERROR_STUB = "Response body error stub";

    RequestExecutorImpl executorUnderTest = new RequestExecutorImpl();

    @Mock Request mockedRequest;
    @Mock HttpURLConnection mockedConnection;
    @Mock OutputStream mockedOutputStream;

    @BeforeEach
    void setUp() throws IOException {
        lenient().when(mockedRequest.openConnection()).thenReturn(mockedConnection);
        lenient().when(mockedRequest.getUrl()).thenReturn(null);
        lenient().when(mockedRequest.getHttpBody()).thenReturn(HTTP_REQUEST_BODY_STUB);
        lenient().when(mockedConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        lenient().when(mockedConnection.getErrorStream())
                .thenReturn(new ByteArrayInputStream(HTTP_RESPONSE_BODY_ERROR_STUB.getBytes()));
        lenient().when(mockedConnection.getInputStream())
                .thenReturn(new ByteArrayInputStream(HTTP_RESPONSE_BODY_NORMAL_STUB.getBytes()));
        lenient().when(mockedConnection.getOutputStream())
                .thenReturn(mockedOutputStream);
    }

    @Test
    void execute_shouldPerformCorrectly_ifTheHTTPMethodIsNotGetAndResponseCodeIsOk() throws IOException {
        when(mockedRequest.getHttpMethod()).thenReturn(Request.HTTP_METHOD_PUT);

        Response actualResponse = executorUnderTest.execute(mockedRequest);

        assertThat(actualResponse.isConnectionSuccessful()).isTrue();
        assertThat(actualResponse.getHttpBody()).isEqualTo(HTTP_RESPONSE_BODY_NORMAL_STUB);
        assertThat(actualResponse.getHttpResponseCode()).isEqualTo(HttpURLConnection.HTTP_OK);

        InOrder executionOrder = inOrder(mockedRequest, mockedConnection, mockedOutputStream);
        executionOrder.verify(mockedRequest).openConnection();
        executionOrder.verify(mockedRequest).getHttpMethod();
        executionOrder.verify(mockedConnection).setRequestMethod(Request.HTTP_METHOD_PUT);
        executionOrder.verify(mockedConnection).setRequestProperty(Request.HTTP_HEADER_CONTENT_TYPE_KEY,
                Request.HTTP_HEADER_CONTENT_TYPE_VALUE);
        executionOrder.verify(mockedRequest).getHttpMethod();
        executionOrder.verify(mockedConnection).setDoOutput(true);
        executionOrder.verify(mockedConnection).getOutputStream();
        executionOrder.verify(mockedRequest).getHttpBody();
        executionOrder.verify(mockedOutputStream).write(any(), anyInt(), anyInt());
        executionOrder.verify(mockedOutputStream).close();
        executionOrder.verify(mockedConnection).connect();
        executionOrder.verify(mockedConnection).getResponseCode();
        executionOrder.verify(mockedConnection).getInputStream();
        executionOrder.verify(mockedConnection).disconnect();

        verifyNoMoreInteractions(mockedRequest);
        verifyNoMoreInteractions(mockedConnection);
        verifyNoMoreInteractions(mockedOutputStream);
    }

    @Test
    void execute_shouldPerformCorrectly_ifTheHTTPMethodIsGetAndResponseCodeIsOk() throws IOException {
        when(mockedRequest.getHttpMethod()).thenReturn(Request.HTTP_METHOD_GET);

        Response actualResponse = executorUnderTest.execute(mockedRequest);

        assertThat(actualResponse.isConnectionSuccessful()).isTrue();
        assertThat(actualResponse.getHttpBody()).isEqualTo(HTTP_RESPONSE_BODY_NORMAL_STUB);
        assertThat(actualResponse.getHttpResponseCode()).isEqualTo(HttpURLConnection.HTTP_OK);

        InOrder executionOrder = inOrder(mockedRequest, mockedConnection, mockedOutputStream);
        executionOrder.verify(mockedRequest).openConnection();
        executionOrder.verify(mockedRequest).getHttpMethod();
        executionOrder.verify(mockedConnection).setRequestMethod(Request.HTTP_METHOD_GET);
        executionOrder.verify(mockedConnection).setRequestProperty(Request.HTTP_HEADER_CONTENT_TYPE_KEY,
                Request.HTTP_HEADER_CONTENT_TYPE_VALUE);
        executionOrder.verify(mockedRequest).getHttpMethod();
        executionOrder.verify(mockedConnection).connect();
        executionOrder.verify(mockedConnection).getResponseCode();
        executionOrder.verify(mockedConnection).getInputStream();
        executionOrder.verify(mockedConnection).disconnect();

        verifyNoMoreInteractions(mockedRequest);
        verifyNoMoreInteractions(mockedConnection);
        verifyNoInteractions(mockedOutputStream);
    }

    @Test
    void execute_shouldPerformCorrectly_ifTheHTTPMethodIsGetAndResponseCodeIsNotOk() throws IOException {
        when(mockedRequest.getHttpMethod()).thenReturn(Request.HTTP_METHOD_GET);
        lenient().when(mockedConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        Response actualResponse = executorUnderTest.execute(mockedRequest);

        assertThat(actualResponse.isConnectionSuccessful()).isTrue();
        assertThat(actualResponse.getHttpBody()).isEqualTo(HTTP_RESPONSE_BODY_ERROR_STUB);
        assertThat(actualResponse.getHttpResponseCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);

        InOrder executionOrder = inOrder(mockedRequest, mockedConnection, mockedOutputStream);
        executionOrder.verify(mockedRequest).openConnection();
        executionOrder.verify(mockedRequest).getHttpMethod();
        executionOrder.verify(mockedConnection).setRequestMethod(Request.HTTP_METHOD_GET);
        executionOrder.verify(mockedConnection).setRequestProperty(Request.HTTP_HEADER_CONTENT_TYPE_KEY,
                Request.HTTP_HEADER_CONTENT_TYPE_VALUE);
        executionOrder.verify(mockedRequest).getHttpMethod();
        executionOrder.verify(mockedConnection).connect();
        executionOrder.verify(mockedConnection).getResponseCode();
        executionOrder.verify(mockedConnection).getErrorStream();
        executionOrder.verify(mockedConnection).disconnect();

        verifyNoMoreInteractions(mockedRequest);
        verifyNoMoreInteractions(mockedConnection);
        verifyNoInteractions(mockedOutputStream);
    }

    @Test
    void execute_shouldPerformCorrectly_ifAnExceptionIsThrown() throws IOException {
        when(mockedRequest.openConnection()).thenThrow(new RuntimeException());

        Response actualResponse = executorUnderTest.execute(mockedRequest);

        assertThat(actualResponse.isConnectionSuccessful()).isFalse();
        assertThat(actualResponse.getHttpBody()).isEqualTo("");
        assertThat(actualResponse.getHttpResponseCode()).isEqualTo(0);

        verify(mockedRequest).openConnection();

        verifyNoMoreInteractions(mockedRequest);
        verifyNoInteractions(mockedConnection);
        verifyNoInteractions(mockedOutputStream);
    }

}