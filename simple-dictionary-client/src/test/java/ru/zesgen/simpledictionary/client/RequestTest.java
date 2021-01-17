package ru.zesgen.simpledictionary.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestTest {

    static final String HTTP_METHOD_STUB = "METHOD_STUB";
    static final String HTTP_BODY_STUB = "Body stub";

    private URL urlStub;

    Request requestUnderTest;

    @BeforeEach
    void setUp() throws MalformedURLException {
        urlStub = new URL("http://some_path");
        URLConnection connection = mock(URLConnection.class);
        requestUnderTest = new Request(urlStub, HTTP_METHOD_STUB, HTTP_BODY_STUB);
    }

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(requestUnderTest.getHttpBody()).isSameAs(HTTP_BODY_STUB);
        assertThat(requestUnderTest.getHttpMethod()).isSameAs(HTTP_METHOD_STUB);
        assertThat(requestUnderTest.getUrl()).isSameAs(urlStub);
    }

    @Test
    void setUrl_shouldExecuteCorrectly() {
        requestUnderTest.setUrl(null);
        assertThat(requestUnderTest.getUrl()).isNull();
        requestUnderTest.setUrl(urlStub);
        assertThat(requestUnderTest.getUrl()).isSameAs(urlStub);
    }

    @Test
    void setHttpMethod_shouldExecuteCorrectly() {
        requestUnderTest.setHttpMethod(null);
        assertThat(requestUnderTest.getHttpMethod()).isNull();
        requestUnderTest.setHttpMethod(HTTP_METHOD_STUB);
        assertThat(requestUnderTest.getHttpMethod()).isSameAs(HTTP_METHOD_STUB);
    }

    @Test
    void setHttpBody_shouldExecuteCorrectly() {
        requestUnderTest.setHttpBody(null);
        assertThat(requestUnderTest.getHttpBody()).isNull();
        requestUnderTest.setHttpBody(HTTP_BODY_STUB);
        assertThat(requestUnderTest.getHttpBody()).isSameAs(HTTP_BODY_STUB);
    }

    @Test
    void openConnection_shouldReturnNewConnection_ifUrlIsNotNull() throws IOException {
        URLConnection connection = requestUnderTest.openConnection();
        assertThat(connection).isNotNull();
    }

    @Test
    void openConnection_shouldReturnNull_ifUrlIsNull() throws IOException {
        requestUnderTest.setUrl(null);
        URLConnection connection = requestUnderTest.openConnection();
        assertThat(connection).isNull();
    }
}