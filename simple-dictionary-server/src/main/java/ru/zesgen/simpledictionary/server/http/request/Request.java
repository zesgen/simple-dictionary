package ru.zesgen.simpledictionary.server.http.request;

import java.util.*;

public class Request {

    private String httpMethod;
    private String uriPath;
    private Map<String, List<String>> uriParameters;
    private List<String> bodyTextLines;

    public Request() {
        this.httpMethod = "";
        this.uriPath = "";
        this.uriParameters = Collections.emptyMap();
        this.bodyTextLines = Collections.emptyList();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public Map<String, List<String>> getUriParameters() {
        return uriParameters;
    }

    public void setUriParameters(Map<String, List<String>> uriParameters) {
        this.uriParameters = uriParameters;
    }

    public List<String> getBodyTextLines() {
        return bodyTextLines;
    }

    public void setBodyTextLines(List<String> bodyTextLines) {
        this.bodyTextLines = bodyTextLines;
    }
}
