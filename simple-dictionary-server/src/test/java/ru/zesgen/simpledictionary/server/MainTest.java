package ru.zesgen.simpledictionary.server;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.zesgen.simpledictionary.server.utility.*;

import java.io.PrintStream;

import java.util.OptionalInt;

@ExtendWith(MockitoExtension.class)
class MainTest {

    @Mock ApplicationContext mockedApplicationContext;
    @Mock UserInputParser mockedUserInputParser;
    @Mock HttpServer mockedHttpServer;
    @Mock PrintStream mockedPrintStream;

    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(mockedPrintStream);
        Main.applicationContext = mockedApplicationContext;
        Main.userInputParser = mockedUserInputParser;
        lenient().when(mockedApplicationContext.getHttpServer()).thenReturn(mockedHttpServer);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void main_executesCorrectly_withoutArguments() {
        Main.main(new String[0]);

        InOrder executionOrder = inOrder(mockedApplicationContext, mockedHttpServer, mockedPrintStream);
        executionOrder.verify(mockedPrintStream).println(Main.HELLO_MESSAGE);
        executionOrder.verify(mockedPrintStream).println(Main.BEFORE_INIT_MESSAGE);
        executionOrder.verify(mockedApplicationContext).init(Main.DEFAULT_PORT);
        executionOrder.verify(mockedApplicationContext).getHttpServer();
        executionOrder.verify(mockedHttpServer).start();
        executionOrder.verify(mockedPrintStream).println(Main.AFTER_INIT_MESSAGE + Main.DEFAULT_PORT);

        verifyNoMoreInteractions(mockedPrintStream);
        verifyNoMoreInteractions(mockedApplicationContext);
        verifyNoMoreInteractions(mockedHttpServer);
        verifyNoInteractions(mockedUserInputParser);
    }

    @Test
    void main_executesCorrectly_withGoodPortAsFirstParameter() {
        int expectedPort = 12345;
        when(mockedUserInputParser.parseStringAsTcpPort(any())).thenReturn(OptionalInt.of(expectedPort));
        Main.main(new String[]{"" + expectedPort, "someSecondArgument"});

        InOrder executionOrder = inOrder(mockedApplicationContext, mockedHttpServer, mockedPrintStream,
                mockedUserInputParser);
        executionOrder.verify(mockedPrintStream).println(Main.HELLO_MESSAGE);
        executionOrder.verify(mockedUserInputParser).parseStringAsTcpPort("" + expectedPort);
        executionOrder.verify(mockedPrintStream).println(Main.BEFORE_INIT_MESSAGE);
        executionOrder.verify(mockedApplicationContext).init(expectedPort);
        executionOrder.verify(mockedApplicationContext).getHttpServer();
        executionOrder.verify(mockedHttpServer).start();
        executionOrder.verify(mockedPrintStream).println(Main.AFTER_INIT_MESSAGE + expectedPort);

        verifyNoMoreInteractions(mockedPrintStream);
        verifyNoMoreInteractions(mockedApplicationContext);
        verifyNoMoreInteractions(mockedHttpServer);
        verifyNoMoreInteractions(mockedUserInputParser);
    }

    @Test
    void main_shouldThrowException_withoutBadPortAsFirstParameter() {
        String badPortParameter = "66666";
        when(mockedUserInputParser.parseStringAsTcpPort(any())).thenReturn(OptionalInt.empty());
        assertThatThrownBy(() -> Main.main(new String[]{badPortParameter, "someSecondArgument"}))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessage(Main.BAD_PARAMETER_EXCEPTION_MESSAGE + badPortParameter);

        InOrder executionOrder = inOrder(mockedApplicationContext, mockedHttpServer, mockedPrintStream,
                mockedUserInputParser);
        executionOrder.verify(mockedPrintStream).println(Main.HELLO_MESSAGE);
        executionOrder.verify(mockedUserInputParser).parseStringAsTcpPort(badPortParameter);
        executionOrder.verify(mockedPrintStream).println(Main.BAD_PORT_MESSAGE);

        verifyNoMoreInteractions(mockedPrintStream);
        verifyNoMoreInteractions(mockedApplicationContext);
        verifyNoMoreInteractions(mockedHttpServer);
        verifyNoMoreInteractions(mockedUserInputParser);
    }

    @Test
    void main_executesCorrectly_withParameterToShowHelp() {
        String showHelpParameter = "--HeLp";

        Main.main(new String[]{showHelpParameter, "someSecondArgument"});

        InOrder executionOrder = inOrder(mockedApplicationContext, mockedHttpServer, mockedPrintStream,
                mockedUserInputParser);
        executionOrder.verify(mockedPrintStream).println(Main.HELLO_MESSAGE);
        executionOrder.verify(mockedPrintStream).println(Main.HELP_MESSAGE);

        verifyNoMoreInteractions(mockedPrintStream);
        verifyNoInteractions(mockedApplicationContext);
        verifyNoInteractions(mockedHttpServer);
        verifyNoInteractions(mockedUserInputParser);
    }
}