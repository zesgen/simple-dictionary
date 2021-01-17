package ru.zesgen.simpledictionary.server.http.response;

import static org.assertj.core.api.Assertions.*;

import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    void constructor_shouldExecuteCorrectly() {
        Response responseUnderTest = new Response();
        assertThat(responseUnderTest.getResponseCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(responseUnderTest.getContentType()).isEqualTo(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);
        assertThat(responseUnderTest.getTextBody()).isEmpty();
    }

    @Test
    void setResponseCode_shouldExecuteCorrectly() {
        Response responseUnderTest = new Response();
        int someInt = 12345;
        responseUnderTest.setResponseCode(someInt);
        assertThat(responseUnderTest.getResponseCode()).isEqualTo(someInt);
    }

    @Test
    void setTextBody_shouldExecuteCorrectly() {
        Response responseUnderTest = new Response();
        String someString = "Some string";
        responseUnderTest.setTextBody(someString);
        assertThat(responseUnderTest.getTextBody()).isSameAs(someString);
    }

    @Test
    void setContentType_shouldExecuteCorrectly() {
        Response responseUnderTest = new Response();
        String someString = "Some string";
        responseUnderTest.setContentType(someString);
        assertThat(responseUnderTest.getContentType()).isSameAs(someString);
    }

}