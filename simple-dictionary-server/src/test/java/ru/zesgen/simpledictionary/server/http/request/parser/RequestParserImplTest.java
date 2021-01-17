package ru.zesgen.simpledictionary.server.http.request.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.utility.UriParametersParser;

@ExtendWith(MockitoExtension.class)
class RequestParserImplTest {

    static final String HTTP_METHOD_STUB = "Some method";
    static final String PATH_STRING_STUB = "/some_path";
    static final Map<String, List<String>> HTTP_PARAMETER_MAP_STUB = new HashMap<>();
    static final List<String> HTTP_TEXT_BODY_LINES_STUB = new LinkedList<>();

    @Mock UriParametersParser mockedUriParametersParser;
    @Mock HttpRequestBodyReader mockedHttpRequestBodyReader;
    @Mock HttpExchange mockedHttpExchange;
    URI uriStub = URI.create(PATH_STRING_STUB);

    @InjectMocks
    RequestParserImpl parserUnderTest;

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(parserUnderTest.getHttpRequestBodyReader()).isSameAs(mockedHttpRequestBodyReader);
        Assertions.assertThat(parserUnderTest.getUriParametersParser()).isSameAs(mockedUriParametersParser);
    }

    @Test
    void parse_shouldReturnCorrectRequest() {
        when(mockedHttpExchange.getRequestMethod()).thenReturn(HTTP_METHOD_STUB);
        when(mockedHttpExchange.getRequestURI()).thenReturn(uriStub);
        when(mockedUriParametersParser.parse(uriStub)).thenReturn(HTTP_PARAMETER_MAP_STUB);
        when(mockedHttpRequestBodyReader.readAsTextLines(mockedHttpExchange)).thenReturn(HTTP_TEXT_BODY_LINES_STUB);

        Request actualRequest = parserUnderTest.parse(mockedHttpExchange);
        assertThat(actualRequest.getHttpMethod()).isSameAs(HTTP_METHOD_STUB);
        assertThat(actualRequest.getUriPath()).isSameAs(PATH_STRING_STUB);
        assertThat(actualRequest.getBodyTextLines()).isSameAs(HTTP_TEXT_BODY_LINES_STUB);
        assertThat(actualRequest.getUriParameters()).isSameAs(HTTP_PARAMETER_MAP_STUB);
    }
}