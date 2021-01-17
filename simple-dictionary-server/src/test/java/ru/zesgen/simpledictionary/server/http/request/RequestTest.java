package ru.zesgen.simpledictionary.server.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

class RequestTest {

    public static final String SOME_STRING = "Some string";
    Request requestUnderTest = new Request();

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(requestUnderTest.getHttpMethod()).isEqualTo("");
        assertThat(requestUnderTest.getUriPath()).isEqualTo("");
        assertThat(requestUnderTest.getBodyTextLines()).isSameAs(Collections.emptyList());
        assertThat(requestUnderTest.getUriParameters()).isSameAs(Collections.emptyMap());
    }

    @Test
    void setHttpMethod_shouldExecuteCorrectly() {
        requestUnderTest.setHttpMethod(SOME_STRING);
        assertThat(requestUnderTest.getHttpMethod()).isSameAs(SOME_STRING);
    }

    @Test
    void setUriPath_shouldExecuteCorrectly() {
        requestUnderTest.setUriPath(SOME_STRING);
        assertThat(requestUnderTest.getUriPath()).isSameAs(SOME_STRING);
    }

    @Test
    void setBodyTextLines_shouldExecuteCorrectly() {
        List<String> someStrings = Collections.singletonList(SOME_STRING);
        requestUnderTest.setBodyTextLines(someStrings);
        assertThat(requestUnderTest.getBodyTextLines()).isSameAs(someStrings);
    }

    @Test
    void setUriParameters_shouldExecuteCorrectly() {
        Map<String, List<String>> someMap = new HashMap<>();
        requestUnderTest.setUriParameters(someMap);
        assertThat(requestUnderTest.getUriParameters()).isSameAs(someMap);
    }


}