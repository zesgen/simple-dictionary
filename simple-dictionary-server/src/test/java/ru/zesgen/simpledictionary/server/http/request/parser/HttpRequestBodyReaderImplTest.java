package ru.zesgen.simpledictionary.server.http.request.parser;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpRequestBodyReaderImplTest {

    static final String HTTP_REQUEST_BODY_LINE1_STUB = "Http body line 1";
    static final String HTTP_REQUEST_BODY_LINE2_STUB = "Http body line 2";
    static final String NOT_EMPTY_HTTP_REQUEST_BODY_STUB =
            " \t" + HTTP_REQUEST_BODY_LINE1_STUB + " \t\r\n" +
            " \t" + HTTP_REQUEST_BODY_LINE2_STUB + " \t";

    @Mock
    HttpExchange mockedHttpExchange;

    HttpRequestBodyReaderImpl readerUnderTest = new HttpRequestBodyReaderImpl();

    @Test
    void readAsTextLines_shouldReturnCorrectListOfStrings_withHttpExchangeWhichHasNotEmptyRequestBody() {
        when(mockedHttpExchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(NOT_EMPTY_HTTP_REQUEST_BODY_STUB.getBytes()));

        List<String> actualBodyLines = readerUnderTest.readAsTextLines(mockedHttpExchange);
        assertThat(actualBodyLines.size()).isEqualTo(2);
        assertThat(actualBodyLines.get(0)).isEqualTo(HTTP_REQUEST_BODY_LINE1_STUB);
        assertThat(actualBodyLines.get(1)).isEqualTo(HTTP_REQUEST_BODY_LINE2_STUB);
    }

    @Test
    void readAsTextLines_shouldReturnCorrectListOfStrings_withHttpExchangeWhichHasEmptyRequestBody() {
        when(mockedHttpExchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream("".getBytes()));

        List<String> actualBodyLines = readerUnderTest.readAsTextLines(mockedHttpExchange);
        assertThat(actualBodyLines).isEmpty();
    }

    @Test
    void readAsTextLines_shouldReturnCorrectListOfStrings_withHttpExchangeWhichHasRequestBodyWithWhitespacesOnly() {
        when(mockedHttpExchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(" \t \t \t \t".getBytes()));

        List<String> actualBodyLines = readerUnderTest.readAsTextLines(mockedHttpExchange);
        assertThat(actualBodyLines.size()).isEqualTo(1);
        assertThat(actualBodyLines.get(0)).isEqualTo("");
    }

    @Test
    void readAsTextLines_shouldThrowException_ifHttpExchangeHasRequestBodyThatThrowsExceptionDuringRead()
            throws IOException {
        IOException exceptionStub = new IOException("Exception message stub");
        InputStream mockedIncInputStream = mock(InputStream.class);
        when(mockedHttpExchange.getRequestBody()).thenReturn(mockedIncInputStream);
        when(mockedIncInputStream.read(any(), anyInt(), anyInt())).thenThrow(exceptionStub);

        assertThatThrownBy(() -> readerUnderTest.readAsTextLines(mockedHttpExchange))
                .isExactlyInstanceOf(UncheckedIOException.class)
                .hasCause(exceptionStub);
    }
}