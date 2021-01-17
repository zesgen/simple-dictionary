package ru.zesgen.simpledictionary.server.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.request.handlers.RequestHandler;
import ru.zesgen.simpledictionary.server.http.request.parser.RequestParser;
import ru.zesgen.simpledictionary.server.http.response.Response;
import ru.zesgen.simpledictionary.server.utility.ExceptionProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ServerHttpHandlerTest {

    static final String HTTP_METHOD_TO_HANDLE_STUB = "METHOD";
    static final String HTTP_URI_PATH_TO_HANDLE_STUB = "/some_path";
    static final List<String> HTTP_URI_PATHS_TO_HANDLE_STUB = Collections.singletonList(HTTP_URI_PATH_TO_HANDLE_STUB);

    static final int RESPONSE_CODE_STUB = 123;
    static final String RESPONSE_TEXT_BODY_CONTENT_STUB = "Some content";
    static final String CONTENT_TYPE_STUB = "Some content type";
    static final String EXCEPTION_MESSAGE_STUB = "Exception message stub";

    @Mock HttpExchange mockedHttpExchange;
    @Mock OutputStream mockedOutputStream;
    @Mock Headers mockHeaders;
    @Mock Request mockedRequest;

    private boolean handlerExecutionReturnValue;

    private final RequestHandler requestHandler =
            new RequestHandler(HTTP_METHOD_TO_HANDLE_STUB, HTTP_URI_PATHS_TO_HANDLE_STUB) {
                @Override
                public boolean process(Request request, Response response) {
                    response.setResponseCode(RESPONSE_CODE_STUB);
                    response.setTextBody(RESPONSE_TEXT_BODY_CONTENT_STUB);
                    response.setContentType(CONTENT_TYPE_STUB);
                    return handlerExecutionReturnValue;
                }
            };

    private final RequestParser mockedRequestParser = mock(RequestParser.class);
    private final ExceptionProcessor mockedExceptionProcessor = mock(ExceptionProcessor.class);
    private final List<RequestHandler> requestHandlersStub = Collections.singletonList(requestHandler);

    private final ServerHttpHandler handlerUnderTest =
            new ServerHttpHandler(requestHandlersStub, mockedRequestParser, mockedExceptionProcessor);

    @BeforeEach
    void setUp() {
        lenient().when(mockedHttpExchange.getResponseBody()).thenReturn(mockedOutputStream);
        lenient().when(mockedHttpExchange.getResponseHeaders()).thenReturn(mockHeaders);
        lenient().when(mockedRequestParser.parse(any())).thenReturn(mockedRequest);
        lenient().when(mockedExceptionProcessor.extractStackTrace(any())).thenReturn(EXCEPTION_MESSAGE_STUB);
    }

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(handlerUnderTest.getExceptionProcessor()).isSameAs(mockedExceptionProcessor);
        assertThat(handlerUnderTest.getRequestHandlers()).isSameAs(requestHandlersStub);
        assertThat(handlerUnderTest.getRequestParser()).isSameAs(mockedRequestParser);
    }

    @Test
    void handle_shouldPrepareEndSendExpectedResponse_ifThereIsASuitableRequestHandler() throws IOException {
        handlerExecutionReturnValue = true;

        handlerUnderTest.handle(mockedHttpExchange);

        InOrder executionOrder = inOrder(mockedRequestParser, mockedHttpExchange, mockHeaders, mockedOutputStream);
        executionOrder.verify(mockedRequestParser).parse(mockedHttpExchange);
        executionOrder.verify(mockedHttpExchange).getResponseHeaders();
        executionOrder.verify(mockHeaders).add(ServerHttpHandler.CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_STUB);
        executionOrder.verify(mockedHttpExchange)
                .sendResponseHeaders(RESPONSE_CODE_STUB, RESPONSE_TEXT_BODY_CONTENT_STUB.getBytes().length);
        executionOrder.verify(mockedHttpExchange).getResponseBody();
        executionOrder.verify(mockedOutputStream).write(RESPONSE_TEXT_BODY_CONTENT_STUB.getBytes());
        executionOrder.verify(mockedHttpExchange).close();

        verifyNoMoreInteractions(mockedRequestParser);
        verifyNoMoreInteractions(mockedHttpExchange);
        verifyNoMoreInteractions(mockHeaders);
        verifyNoMoreInteractions(mockedOutputStream);
    }

    @Test
    void handle_shouldPrepareEndSendExpectedResponse_ifThereIsNoSuitableRequestHandler() throws IOException {
        handlerExecutionReturnValue = false;

        handlerUnderTest.handle(mockedHttpExchange);

        InOrder executionOrder = inOrder(mockedRequestParser, mockedHttpExchange, mockHeaders, mockedOutputStream);
        executionOrder.verify(mockedRequestParser).parse(mockedHttpExchange);
        executionOrder.verify(mockedHttpExchange).getResponseHeaders();
        executionOrder.verify(mockHeaders)
                .add(ServerHttpHandler.CONTENT_TYPE_HEADER_KEY, Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        executionOrder.verify(mockedHttpExchange)
                .sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND,
                        ServerHttpHandler.MESSAGE_IF_NO_HANDLER_FOUND.getBytes().length);
        executionOrder.verify(mockedHttpExchange).getResponseBody();
        executionOrder.verify(mockedOutputStream).write(ServerHttpHandler.MESSAGE_IF_NO_HANDLER_FOUND.getBytes());
        executionOrder.verify(mockedHttpExchange).close();

        verifyNoMoreInteractions(mockedRequestParser);
        verifyNoMoreInteractions(mockedHttpExchange);
        verifyNoMoreInteractions(mockHeaders);
        verifyNoMoreInteractions(mockedOutputStream);
    }

    @Test
    void handle_shouldPrepareEndSendExpectedResponse_ifAnExceptionHasBeenThrownDuringHandling() throws IOException {
        RuntimeException runtimeException = new RuntimeException("");
        handlerExecutionReturnValue = true;
        when(mockedRequestParser.parse(any())).thenThrow(runtimeException);
        String expectedResponseBodyContent = ServerHttpHandler.MESSAGE_IF_AN_EXCEPTION_HAS_BEEN_THROWN +
                Response.BODY_NEWLINE + EXCEPTION_MESSAGE_STUB;

        handlerUnderTest.handle(mockedHttpExchange);

        InOrder executionOrder = inOrder(mockedRequestParser, mockedHttpExchange, mockHeaders, mockedOutputStream,
                mockedExceptionProcessor);
        executionOrder.verify(mockedRequestParser).parse(mockedHttpExchange);
        executionOrder.verify(mockedExceptionProcessor).extractStackTrace(runtimeException);
        executionOrder.verify(mockedHttpExchange).getResponseHeaders();
        executionOrder.verify(mockHeaders)
                .add(ServerHttpHandler.CONTENT_TYPE_HEADER_KEY, Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        executionOrder.verify(mockedHttpExchange)
                .sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR,
                        expectedResponseBodyContent.getBytes().length);
        executionOrder.verify(mockedHttpExchange).getResponseBody();
        executionOrder.verify(mockedOutputStream).write(expectedResponseBodyContent.getBytes());
        executionOrder.verify(mockedHttpExchange).close();

        verifyNoMoreInteractions(mockedRequestParser);
        verifyNoMoreInteractions(mockedExceptionProcessor);
        verifyNoMoreInteractions(mockedHttpExchange);
        verifyNoMoreInteractions(mockHeaders);
        verifyNoMoreInteractions(mockedOutputStream);
    }

}