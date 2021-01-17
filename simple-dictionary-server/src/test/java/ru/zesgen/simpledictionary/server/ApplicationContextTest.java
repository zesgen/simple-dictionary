package ru.zesgen.simpledictionary.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sun.net.httpserver.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zesgen.simpledictionary.server.http.ServerHttpHandler;

import java.util.concurrent.ThreadPoolExecutor;

@ExtendWith(MockitoExtension.class)
class ApplicationContextTest {

    ApplicationContext contextUnderTest = new ApplicationContext();

    @Test
    void constructor_executesCorrectly() {
        assertThat(contextUnderTest.getHttpContext()).isNull();
        assertThat(contextUnderTest.getHttpServer()).isNull();
        assertThat(contextUnderTest.isInitialized()).isFalse();
    }

    @Test
    void init_shouldExecutesCorrectly_withAllowedPort() {
        int somePort = 12345;

        contextUnderTest.init(somePort);

        HttpContext httpContext = contextUnderTest.getHttpContext();
        HttpServer httpServer = contextUnderTest.getHttpServer();
        HttpHandler httpHandler = httpContext.getHandler();

        assertThat(contextUnderTest.isInitialized()).isTrue();
        assertThat(httpContext.getPath()).isEqualTo("/");
        assertThat(httpServer).isNotNull();
        assertThat(httpServer.getAddress().getPort()).isEqualTo(somePort);
        assertThat(httpHandler).isExactlyInstanceOf(ServerHttpHandler.class);
        assertThat(((ServerHttpHandler) httpHandler).getRequestHandlers().size()).isEqualTo(4);
        assertThat(((ServerHttpHandler) httpHandler).getRequestParser()).isNotNull();
        assertThat(((ServerHttpHandler) httpHandler).getExceptionProcessor()).isNotNull();
        assertThat(httpServer.getExecutor()).isNotNull();
        assertThat(httpServer.getExecutor()).isExactlyInstanceOf(ThreadPoolExecutor.class);
    }

    @Test
    void init_shouldThrowException_withZeroPort() {
        int zeroPort = 0;

        contextUnderTest.init(zeroPort);

        HttpServer httpServer = contextUnderTest.getHttpServer();
        assertThat(httpServer.getAddress().getPort()).isNotEqualTo(zeroPort);

    }

    @Test
    void init_shouldThrowException_withInvalidPort() {
        int invalidPort = 65536;

        assertThatThrownBy(() -> contextUnderTest.init(invalidPort))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }
}