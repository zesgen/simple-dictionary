package ru.zesgen.simpledictionary.client;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Request {

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String HTTP_HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String HTTP_HEADER_CONTENT_TYPE_VALUE = "text/plain; charset=utf-8";

    private URL url;
    private String httpMethod;
    private String httpBody;

    public Request(URL url, String httpMethod, String httpBody) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.httpBody = httpBody;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpBody() {
        return httpBody;
    }

    public void setHttpBody(String httpBody) {
        this.httpBody = httpBody;
    }

    public URLConnection openConnection() throws IOException {
        if (url != null) {
            return url.openConnection();
        } else {
            return null;
        }
    }
}
