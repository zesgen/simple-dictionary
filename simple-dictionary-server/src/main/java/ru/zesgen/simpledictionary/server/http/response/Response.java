package ru.zesgen.simpledictionary.server.http.response;

import java.net.HttpURLConnection;

public class Response {

    public static final String CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT = "text/plain; charset=utf-8";
    public static final String CONTENT_TYPE_HEADER_VALUE_FOR_HTML = "text/html; charset=utf-8";
    public static final String BODY_NEWLINE = "\n";

    private int responseCode;
    private String textBody;
    private String contentType;

    public Response() {
        this.responseCode = HttpURLConnection.HTTP_OK;
        this.textBody = "";
        this.contentType = CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
