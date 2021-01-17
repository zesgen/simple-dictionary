package ru.zesgen.simpledictionary.client;

import java.net.MalformedURLException;
import java.net.URL;

public class UserInputParserImpl implements UserInputParser {

    static final String COMMAND_ADD = "add";
    static final String COMMAND_GET = "get";
    static final String COMMAND_DELETE = "delete";

    static final int ARG_INDEX_OF_HOST = 0;
    static final int ARG_INDEX_OF_PORT = 1;
    static final int ARG_INDEX_OF_COMMAND = 2;
    static final int ARG_INDEX_OF_WORD = 3;
    static final int ARG_INDEX_OF_MEANING1 = 4;

    static final String PROTOCOL = "http://";
    static final String URI_PATH = "/dictionary/meanings";
    static final String URI_PARAMETERS = "?for_word=";

    private static class Parameters {
        public String host;
        public String port;
        public String command;
        public String word;
        public String meaning1;
    }

    @Override
    public Request parseAsRequestOrNull(String[] args) {
        if (args.length < 4) {
            return null;
        }

        Parameters parameters = parseParameters(args);
        String httMethod = getHttpMethodOrNull(args, parameters.command);
        URL url = makeUrlOrNull(parameters);
        String httpBody = makeHttpBodyOrNull(args, parameters.command);
        if (httMethod == null || url == null || httpBody == null) {
            return null;
        }

        return new Request(url, httMethod, httpBody);
    }

    private Parameters parseParameters(String[] args) {
        Parameters parameters = new Parameters();
        parameters.host = args[ARG_INDEX_OF_HOST].trim();
        parameters.port = args[ARG_INDEX_OF_PORT].trim();
        parameters.command = args[ARG_INDEX_OF_COMMAND].trim();
        parameters.word = args[ARG_INDEX_OF_WORD].trim();
        return parameters;
    }

    private String getHttpMethodOrNull(String[] args, String command) {
        if (command.equals(COMMAND_GET)) {
            return Request.HTTP_METHOD_GET;
        } else if (command.equals(COMMAND_ADD)) {
            if (args.length < (ARG_INDEX_OF_MEANING1 + 1)) {
                return null;
            }
            return Request.HTTP_METHOD_PUT;
        } else if (command.equals(COMMAND_DELETE)) {
            if (args.length < (ARG_INDEX_OF_MEANING1 + 1)) {
                return null;
            }
            return Request.HTTP_METHOD_DELETE;
        } else {
            return null;
        }
    }

    private URL makeUrlOrNull(Parameters parameters) {
        try {
            return new URL(PROTOCOL + parameters.host + ":" + parameters.port +
                    URI_PATH + URI_PARAMETERS + parameters.word);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private String makeHttpBodyOrNull(String[] args, String command) {
        if (command.equals(COMMAND_GET)) {
            return "";
        }
        StringBuilder httpBody = new StringBuilder();
        for (int i = ARG_INDEX_OF_MEANING1; i < args.length; ++i) {
            String meaning = args[i].trim();
            if (!meaning.isEmpty()) {
                httpBody.append(meaning);
                httpBody.append("\n");
            }
        }
        if (httpBody.length() == 0) {
            return null;
        } else {
            return httpBody.toString().trim();
        }
    }

}
