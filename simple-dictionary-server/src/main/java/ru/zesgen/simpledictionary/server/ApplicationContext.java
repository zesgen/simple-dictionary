package ru.zesgen.simpledictionary.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import ru.zesgen.simpledictionary.server.dao.Dictionary;
import ru.zesgen.simpledictionary.server.http.ServerHttpHandler;
import ru.zesgen.simpledictionary.server.http.request.handlers.*;
import ru.zesgen.simpledictionary.server.http.request.parser.*;
import ru.zesgen.simpledictionary.server.utility.UriParametersParser;
import ru.zesgen.simpledictionary.server.utility.ExceptionProcessor;
import ru.zesgen.simpledictionary.server.utility.ExceptionProcessorImpl;
import ru.zesgen.simpledictionary.server.utility.UriParametersParserImpl;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ApplicationContext {

    private HttpServer httpServer;
    private HttpContext httpContext;
    private boolean isInitialized = false;

    void init(int portToListen) {
        isInitialized = false;

        Dictionary dictionary = new Dictionary();

        List<RequestHandler> requestHandlers = makeRequestHandlers(dictionary);
        ServerHttpHandler serverHttpHandler = makeServerHttpHandler(requestHandlers);
        try {
            httpServer = HttpServer.create(new InetSocketAddress(portToListen), 0);
        } catch (Throwable e) {
            throw new RuntimeException("Exception during server creation. ", e);
        }
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpContext = httpServer.createContext("/", serverHttpHandler);

        isInitialized = true;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private ServerHttpHandler makeServerHttpHandler(List<RequestHandler> requestHandlers) {
        UriParametersParser uriParametersParser = new UriParametersParserImpl(StandardCharsets.UTF_8.displayName());
        HttpRequestBodyReader httpRequestBodyReader =
                new HttpRequestBodyReaderImpl();
        RequestParser requestParser = new RequestParserImpl(uriParametersParser, httpRequestBodyReader);
        ExceptionProcessor exceptionProcessor = new ExceptionProcessorImpl();
        return new ServerHttpHandler(requestHandlers, requestParser, exceptionProcessor);
    }

    private List<RequestHandler> makeRequestHandlers(Dictionary dictionary) {
        List<RequestHandler> requestHandlers = new ArrayList<>(4);
        RequestHandler requestHandler = new MeaningsGettingRequestHandler(dictionary);
        requestHandlers.add(requestHandler);
        requestHandler = new MeaningsAdditionRequestHandler(dictionary);
        requestHandlers.add(requestHandler);
        requestHandler = new MeaningsDeletingRequestHandler(dictionary);
        requestHandlers.add(requestHandler);
        requestHandler = new InfoRequestHandler(ApplicationContext.class.getClassLoader());
        requestHandlers.add(requestHandler);
        return requestHandlers;
    }
}
