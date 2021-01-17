package ru.zesgen.simpledictionary.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.security.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainTest {

    static final String RESPONSE_VIEW_STUB = "Response view stub";

    @Mock private UserInputParser mockedUserInputParser;
    @Mock private RequestExecutor mockedRequestExecutor;
    @Mock private ResponseViewer mockedResponseViewer;
    @Mock private PrintStream mockedPrintStream;
    @Mock private Request mockedRequest;
    @Mock private Response mockedResponse;

    private final PrintStream originalOut = System.out;

    static class ExitException extends SecurityException {
        private static final long serialVersionUID = 1L;

        public final int status;

        public ExitException(int status) {
            super("There is no exit while testing!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            //This function need to prevent SecurityManager exception
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    @BeforeEach
    void setUp() {
        System.setOut(mockedPrintStream);
        System.setSecurityManager(new NoExitSecurityManager());

        Main.userInputParser = mockedUserInputParser;
        Main.requestExecutor = mockedRequestExecutor;
        Main.responseViewer = mockedResponseViewer;

        lenient().when(mockedUserInputParser.parseAsRequestOrNull(any())).thenReturn(mockedRequest);
        lenient().when(mockedRequestExecutor.execute(any())).thenReturn(mockedResponse);
        lenient().when(mockedResponseViewer.makeView(anyString(), any())).thenReturn(RESPONSE_VIEW_STUB);
        lenient().when(mockedResponse.getHttpResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setSecurityManager(null);
    }

    @Test
    void constructorExecutesCorrectly() {
        Main main = new Main();
        assertThat(main).isNotNull();
    }

    @Test
    void main_shouldExecuteCorrectly_ifUserInputIsCorrectAndServerRespondsOk() {
        String[] args = new String[]{"host", "port", "command", "word", "meaning"};

        Main.main(args);

        InOrder executionOrder = inOrder(mockedUserInputParser, mockedRequestExecutor,
                mockedResponseViewer, mockedPrintStream);
        executionOrder.verify(mockedUserInputParser).parseAsRequestOrNull(args);
        executionOrder.verify(mockedRequestExecutor).execute(mockedRequest);
        executionOrder.verify(mockedResponseViewer).makeView(args[2], mockedResponse);
        executionOrder.verify(mockedPrintStream).println(RESPONSE_VIEW_STUB);

        verifyNoMoreInteractions(mockedUserInputParser);
        verifyNoMoreInteractions(mockedRequestExecutor);
        verifyNoMoreInteractions(mockedResponseViewer);
        verifyNoMoreInteractions(mockedPrintStream);
    }

    @Test
    void main_shouldExecuteCorrectly_ifUserInputIsCorrectAndServerRespondsBad() {
        when(mockedResponse.getHttpResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        String[] args = new String[]{"host", "port", "command", "word", "meaning"};

        try {
            Main.main(args);
        } catch (ExitException e) {
            assertThat(e.status).describedAs("Exit code check").isEqualTo(Main.ERROR_EXIT_CODE);
        }

        InOrder executionOrder = inOrder(mockedUserInputParser, mockedRequestExecutor,
                mockedResponseViewer, mockedPrintStream);
        executionOrder.verify(mockedUserInputParser).parseAsRequestOrNull(args);
        executionOrder.verify(mockedRequestExecutor).execute(mockedRequest);
        executionOrder.verify(mockedResponseViewer).makeView(args[2], mockedResponse);
        executionOrder.verify(mockedPrintStream).println(RESPONSE_VIEW_STUB);

        verifyNoMoreInteractions(mockedUserInputParser);
        verifyNoMoreInteractions(mockedRequestExecutor);
        verifyNoMoreInteractions(mockedResponseViewer);
        verifyNoMoreInteractions(mockedPrintStream);
    }

    @Test
    void main_shouldExecuteCorrectly_ifUserInputIsIncorrect() {
        when(mockedUserInputParser.parseAsRequestOrNull(any())).thenReturn(null);
        String[] args = new String[]{"host", "port", "command", "word", "meaning"};

        try {
            Main.main(args);
        } catch (ExitException e) {
            assertThat(e.status).describedAs("Exit code check").isEqualTo(Main.ERROR_EXIT_CODE);
        }

        InOrder executionOrder = inOrder(mockedUserInputParser, mockedRequestExecutor,
                mockedResponseViewer, mockedPrintStream);
        executionOrder.verify(mockedUserInputParser).parseAsRequestOrNull(args);
        executionOrder.verify(mockedPrintStream).println(Main.MESSAGE_IF_BAD_PARAMETERS);

        verifyNoMoreInteractions(mockedUserInputParser);
        verifyNoInteractions(mockedRequestExecutor);
        verifyNoInteractions(mockedResponseViewer);
        verifyNoMoreInteractions(mockedPrintStream);
    }
}
