package ru.zesgen.simpledictionary.client;

public class Response {

    private boolean isConnectionSuccessful;
    private int httpResponseCode;
    private String httpBody;

    public Response(boolean isConnectionSuccessful, int httpResponseCode, String httpBody) {
        this.isConnectionSuccessful = isConnectionSuccessful;
        this.httpResponseCode = httpResponseCode;
        this.httpBody = httpBody;
    }

    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }

    public void setConnectionSuccessful(boolean connectionSuccessful) {
        isConnectionSuccessful = connectionSuccessful;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public String getHttpBody() {
        return httpBody;
    }

    public void setHttpBody(String httpBody) {
        this.httpBody = httpBody;
    }
}
