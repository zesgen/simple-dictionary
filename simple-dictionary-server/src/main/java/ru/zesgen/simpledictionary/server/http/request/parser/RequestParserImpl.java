package ru.zesgen.simpledictionary.server.http.request.parser;

import com.sun.net.httpserver.HttpExchange;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.utility.UriParametersParser;

public class RequestParserImpl implements RequestParser {

    private final UriParametersParser uriParametersParser;
    private final HttpRequestBodyReader httpRequestBodyReader;

    public RequestParserImpl(UriParametersParser uriParametersParser, HttpRequestBodyReader httpRequestBodyReader) {
        this.uriParametersParser = uriParametersParser;
        this.httpRequestBodyReader = httpRequestBodyReader;
    }

    @Override
    public Request parse(HttpExchange httpExchange) {
        Request request = new Request();
        request.setHttpMethod(httpExchange.getRequestMethod());
        request.setUriPath(httpExchange.getRequestURI().getPath());
        request.setUriParameters(uriParametersParser.parse(httpExchange.getRequestURI()));
        request.setBodyTextLines(httpRequestBodyReader.readAsTextLines(httpExchange));
        return request;
    }

    public UriParametersParser getUriParametersParser() {
        return uriParametersParser;
    }

    public HttpRequestBodyReader getHttpRequestBodyReader() {
        return httpRequestBodyReader;
    }
}
