package ru.zesgen.simpledictionary.client;

import java.net.*;

public class Main {
    static final int ERROR_EXIT_CODE = 1;
    static final String NEWLINE = System.lineSeparator();
    static final String MESSAGE_IF_BAD_PARAMETERS =
            "Wrong parameters have been entered." + NEWLINE +
            "Check them and try again. " + NEWLINE +
            "See the 'readme.md' file for help.";

    static UserInputParser userInputParser = new UserInputParserImpl();
    static RequestExecutor requestExecutor = new RequestExecutorImpl();
    static ResponseViewer responseViewer = new ResponseViewerImpl();

    public static void main(String[] args) {
        Request request = userInputParser.parseAsRequestOrNull(args);
        if (request != null) {
            String command = args[UserInputParserImpl.ARG_INDEX_OF_COMMAND].trim();
            Response response = requestExecutor.execute(request);
            String responseView = responseViewer.makeView(command, response);
            System.out.println(responseView);
            if (response.getHttpResponseCode() != HttpURLConnection.HTTP_OK)
                System.exit(ERROR_EXIT_CODE);
        } else {
            System.out.println(MESSAGE_IF_BAD_PARAMETERS);
            System.exit(ERROR_EXIT_CODE);
        }
    }

}
