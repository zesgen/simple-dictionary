package ru.zesgen.simpledictionary.client;

import java.net.HttpURLConnection;

public class ResponseViewerImpl implements ResponseViewer {

    public static final String NEWLINE = System.lineSeparator();
    public static final String MESSAGE_IF_CONNECTION_FAILED =
            "[ERR ] Can't connect to the server. Check parameters and try again";
    public static final String MESSAGE_IF_ADDITION_IS_SUCCESSFUL =
            "[ OK ] The word meaning(s) has(have) been successfully added.";
    public static final String MESSAGE_IF_DELETING_IS_SUCCESSFUL =
            "[ OK ] The word meaning(s) has(have) been successfully deleted.";
    public static final String MESSAGE_IF_GETTING_FAILED =
            "[ERR ] The word is not in the dictionary";
    public static final String MESSAGE_IF_ADDITION_FAILED =
            "[ERR ] The word meanings addition failed. " + NEWLINE +
                    "The message from Server:" + NEWLINE;
    public static final String MESSAGE_IF_DELETING_FAILED =
            "[ERR ] The word or its meaning(s) are not in the dictionary.";
    public static final String MESSAGE_IF_BAD_SERVER_RESPONSE =
            "[ERR ] The server has responded with unexpected HTTP status code: ";
    public static final String SERVER_RESPONSE_TITLE = "The message from server:" + NEWLINE;
    public static final String MESSAGE_IF_APPLICATION_ERROR =
            "[ERR ] The Application error. You should not see this :)";


    @Override
    public String makeView(String command, Response response) {
        if (!response.isConnectionSuccessful()) {
            return MESSAGE_IF_CONNECTION_FAILED;
        }
        if (response.getHttpResponseCode() == HttpURLConnection.HTTP_OK) {
            if (command.equals(UserInputParserImpl.COMMAND_GET)) {
                return response.getHttpBody();
            } else if (command.equals(UserInputParserImpl.COMMAND_ADD)) {
                return MESSAGE_IF_ADDITION_IS_SUCCESSFUL;
            } else if (command.equals(UserInputParserImpl.COMMAND_DELETE)) {
                return MESSAGE_IF_DELETING_IS_SUCCESSFUL;
            } else {
                return MESSAGE_IF_APPLICATION_ERROR;
            }
        } else if (response.getHttpResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            if (command.equals(UserInputParserImpl.COMMAND_GET)) {
                return MESSAGE_IF_GETTING_FAILED;
            } else if (command.equals(UserInputParserImpl.COMMAND_ADD)) {
                return MESSAGE_IF_ADDITION_FAILED + response.getHttpBody();
            } else if (command.equals(UserInputParserImpl.COMMAND_DELETE)) {
                return MESSAGE_IF_DELETING_FAILED;
            } else {
                return MESSAGE_IF_APPLICATION_ERROR;
            }
        } else {
            return MESSAGE_IF_BAD_SERVER_RESPONSE + response.getHttpResponseCode() + "." + NEWLINE +
                    SERVER_RESPONSE_TITLE + response.getHttpBody();
        }
    }
}
