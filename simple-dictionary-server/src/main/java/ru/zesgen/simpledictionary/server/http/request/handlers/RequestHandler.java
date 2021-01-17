package ru.zesgen.simpledictionary.server.http.request.handlers;

import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.util.List;

public abstract class RequestHandler {

    protected final String httpMethodToHandle;
    protected final List<String> uriPathsToHandle;

    public RequestHandler(String httpMethodToHandle, List<String> uriPathsToHandle) {
        this.httpMethodToHandle = httpMethodToHandle;
        this.uriPathsToHandle = uriPathsToHandle;
    }

    public abstract boolean process(Request request, Response response);

    public boolean checkCompliance(Request request) {
        boolean isMethodCompliance = httpMethodToHandle.equals(request.getHttpMethod());
        boolean isAnyPathCompliance = uriPathsToHandle.stream()
                .anyMatch(path -> path.equals(request.getUriPath()));
        return isMethodCompliance && isAnyPathCompliance;
    }

    public String getHttpMethodToHandle() {
        return httpMethodToHandle;
    }

    public List<String> getUriPathsToHandle() {
        return uriPathsToHandle;
    }
}
