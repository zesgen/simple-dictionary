package ru.zesgen.simpledictionary.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.request.handlers.RequestHandler;
import ru.zesgen.simpledictionary.server.http.request.parser.RequestParser;
import ru.zesgen.simpledictionary.server.http.response.Response;
import ru.zesgen.simpledictionary.server.utility.ExceptionProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class ServerHttpHandler implements HttpHandler {

    public static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    public static final String MESSAGE_IF_NO_HANDLER_FOUND =
            "No handler found for the request." + Response.BODY_NEWLINE +
            "Check the request method is being used. " + Response.BODY_NEWLINE +
            "Or check the HTTP request path. " + Response.BODY_NEWLINE +
            "Or check the HTTP request parameters are being sent." + Response.BODY_NEWLINE +
            "For more information try to request '/' from the server.";
    public static final String MESSAGE_IF_AN_EXCEPTION_HAS_BEEN_THROWN =
            "An exception has been thrown while request handling. The stack trace is:";


    private final List<RequestHandler> requestHandlers;
    private final RequestParser requestParser;
    private final ExceptionProcessor exceptionProcessor;

    public ServerHttpHandler(List<RequestHandler> requestHandlers, RequestParser requestParser,
                             ExceptionProcessor exceptionProcessor) {
        this.requestHandlers = requestHandlers;
        this.requestParser = requestParser;
        this.exceptionProcessor = exceptionProcessor;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Response response = new Response();
        try {
            Request request = requestParser.parse(httpExchange);

            boolean isHandlerFound =
                    requestHandlers.stream().anyMatch(requestHandler -> requestHandler.process(request, response));
            if (!isHandlerFound) {
                prepareResponseIfNoHandlerFound(response);
            }
        } catch (Exception e) {
            prepareResponseIfExceptionThrownWhileHandling(response, e);
        }

        httpExchange.getResponseHeaders().add(CONTENT_TYPE_HEADER_KEY, response.getContentType());
        httpExchange.sendResponseHeaders(response.getResponseCode(), response.getTextBody().getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getTextBody().getBytes());

        httpExchange.close();
    }

    public List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }

    public RequestParser getRequestParser() {
        return requestParser;
    }

    public ExceptionProcessor getExceptionProcessor() {
        return exceptionProcessor;
    }

    private void prepareResponseIfNoHandlerFound(Response response) {
        response.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
        response.setTextBody(MESSAGE_IF_NO_HANDLER_FOUND);
        response.setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
    }

    private void prepareResponseIfExceptionThrownWhileHandling(Response response, Throwable exception) {
        response.setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        response.setTextBody(MESSAGE_IF_AN_EXCEPTION_HAS_BEEN_THROWN + Response.BODY_NEWLINE +
                exceptionProcessor.extractStackTrace(exception));
        response.setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
    }
}
