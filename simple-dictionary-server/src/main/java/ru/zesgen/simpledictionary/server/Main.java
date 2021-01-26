package ru.zesgen.simpledictionary.server;

import ru.zesgen.simpledictionary.server.ApplicationContext;
import ru.zesgen.simpledictionary.server.utility.UserInputParser;
import ru.zesgen.simpledictionary.server.utility.UserInputParserImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

public class Main {

    public static final String NEWLINE = System.lineSeparator();
    public static int DEFAULT_PORT = 8080;
    public static final String HELLO_MESSAGE = "The test dictionary server application.";
    public static final String HELP_MESSAGE =
            "Usage: simple-dictionary-server [port]" + NEWLINE +
                    "Where: [port] is an optional port " + NEWLINE +
                    "       of the server to listen to (from 1 to 65535)." + NEWLINE +
                    "       If [port] is not specified the default port " + DEFAULT_PORT + " will be used";
    public static final String BAD_PORT_MESSAGE = "The passed port as parameter is bad. Terminating...";
    public static final String BAD_PARAMETER_EXCEPTION_MESSAGE = "Bad application parameter: ";
    public static final String BEFORE_INIT_MESSAGE = "Initializing ...";
    public static final String AFTER_INIT_MESSAGE =
            "The test dictionary server up and running. It listens to the port: ";
    public static final List<String> showHelpParameterValues
            = Collections.unmodifiableList(Arrays.asList("-help", "--help"));

    static ApplicationContext applicationContext = new ApplicationContext();
    static UserInputParser userInputParser = new UserInputParserImpl();

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        boolean shouldRun = true;

        System.out.println(HELLO_MESSAGE);
        if (args.length > 0) {
            String firstArgument = args[0].trim();
            if (checkShowHelp(firstArgument)) {
                System.out.println(HELP_MESSAGE);
                shouldRun = false;
            } else {
                OptionalInt optionalPort = userInputParser.parseStringAsTcpPort(firstArgument);
                if (optionalPort.isPresent()) {
                    port = optionalPort.getAsInt();
                } else {
                    System.out.println(BAD_PORT_MESSAGE);
                    throw new RuntimeException(BAD_PARAMETER_EXCEPTION_MESSAGE + args[0]);
                }
            }
        }

        if (shouldRun) {
            System.out.println(BEFORE_INIT_MESSAGE);
            applicationContext.init(port);
            applicationContext.getHttpServer().start();
            System.out.println(AFTER_INIT_MESSAGE + port);
        }
    }

    private static boolean checkShowHelp(String firstArgument) {
        return showHelpParameterValues.stream().anyMatch(string -> string.equalsIgnoreCase(firstArgument));
    }

}
