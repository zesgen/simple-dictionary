package ru.zesgen.simpledictionary.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseTest {

    static final boolean IS_CONNECTION_SUCCESSFUL_STUB = true;
    static final int HTT_RESPONSE_CODE_STUB = 12345;
    static final String HTTP_BODY_STUB = "Body stub";

    Response responseUnderTest = new Response(IS_CONNECTION_SUCCESSFUL_STUB, HTT_RESPONSE_CODE_STUB, HTTP_BODY_STUB);

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(responseUnderTest.isConnectionSuccessful()).isTrue();
        assertThat(responseUnderTest.getHttpResponseCode()).isEqualTo(HTT_RESPONSE_CODE_STUB);
        assertThat(responseUnderTest.getHttpBody()).isEqualTo(HTTP_BODY_STUB);
    }

    @Test
    void setConnectionSuccessful_shouldExecuteCorrectly() {
        responseUnderTest.setConnectionSuccessful(false);
        assertThat(responseUnderTest.isConnectionSuccessful()).isFalse();
        responseUnderTest.setConnectionSuccessful(true);
        assertThat(responseUnderTest.isConnectionSuccessful()).isTrue();
    }

    @Test
    void setHttpResponseCode_shouldExecuteCorrectly() {
        responseUnderTest.setHttpResponseCode(0);
        assertThat(responseUnderTest.getHttpResponseCode()).isEqualTo(0);
        responseUnderTest.setHttpResponseCode(HTT_RESPONSE_CODE_STUB);
        assertThat(responseUnderTest.getHttpResponseCode()).isEqualTo(HTT_RESPONSE_CODE_STUB);
    }

    @Test
    void setHttpBody_shouldExecuteCorrectly() {
        responseUnderTest.setHttpBody(null);
        assertThat(responseUnderTest.getHttpBody()).isNull();
        responseUnderTest.setHttpBody(HTTP_BODY_STUB);
        assertThat(responseUnderTest.getHttpBody()).isEqualTo(HTTP_BODY_STUB);
    }

}