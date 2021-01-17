package ru.zesgen.simpledictionary.server.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.junit.jupiter.api.Test;

class UriParametersParserImplTest {

    UriParametersParserImpl parserUnderTest = new UriParametersParserImpl(StandardCharsets.UTF_8.displayName());

    @Test
    void parse_shouldReturnCorrectParameterMap_withUriContainingParametersAsKeyAndValuePairsOnly() {
        URI uri = URI.create("/some_path" +
                "?key1=value1" +
                "&key2=value2" +
                "&key3=%D0%B2%D0%B5%D0%BB%D0%B8%D1%87%D0%B8%D0%BD%D0%B03" +
                "&key1=value4");
        Map<String, List<String>> expectedParameters = new HashMap<>();
        List<String> key1Values = new ArrayList<>(2);
        key1Values.add("value1");
        key1Values.add("value4");
        expectedParameters.put("key1", key1Values);
        expectedParameters.put("key2", Collections.singletonList("value2"));
        expectedParameters.put("key3", Collections.singletonList("величина3"));

        Map<String, List<String>> actualParameters = parserUnderTest.parse(uri);

        assertThat(actualParameters).isEqualTo(expectedParameters);
    }

    @Test
    void parse_shouldReturnCorrectParameterMap_withUriContainingSomeParametersWithKeyOnly() {
        URI uri = URI.create("/some_path" +
                "?key1=value1" +
                "&key2" +  /* the key without a value*/
                "&key3=%D0%B2%D0%B5%D0%BB%D0%B8%D1%87%D0%B8%D0%BD%D0%B03" +
                "&key1=value4");
        Map<String, List<String>> expectedParameters = new HashMap<>();
        List<String> key1Values = new ArrayList<>(2);
        key1Values.add("value1");
        key1Values.add("value4");
        expectedParameters.put("key1", key1Values);
        expectedParameters.put("key2", Collections.singletonList(""));
        expectedParameters.put("key3", Collections.singletonList("величина3"));

        Map<String, List<String>> actualParameters = parserUnderTest.parse(uri);

        assertThat(actualParameters).isEqualTo(expectedParameters);
    }

    @Test
    void parse_shouldReturnEmptyParameterMap_withUtiContainingNoParameters() {
        URI uri = URI.create("/some_path");

        Map<String, List<String>> actualParameters = parserUnderTest.parse(uri);

        assertThat(actualParameters).isEmpty();
    }

    @Test
    void parse_shouldThrowException_withTheParserCreatedWithUnsupportedCharset() {
        UriParametersParserImpl parserUnderTestLocal = new UriParametersParserImpl("");
        URI uri = URI.create("/some_path?key=value");

        assertThatThrownBy(() -> parserUnderTestLocal.parse(uri))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(UnsupportedEncodingException.class);

    }

}