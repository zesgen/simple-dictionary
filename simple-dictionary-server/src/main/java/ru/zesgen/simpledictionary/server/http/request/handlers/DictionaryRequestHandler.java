package ru.zesgen.simpledictionary.server.http.request.handlers;

import ru.zesgen.simpledictionary.server.dao.Dictionary;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.net.HttpURLConnection;
import java.util.List;

public abstract class DictionaryRequestHandler extends RequestHandler {

    public static final String PARAMETER_KEY_OF_WORD_FOR = "for_word";
    public static final String MESSAGE_IF_BAD_WORD_PARAMETER =
            "The parameter of the URI query part with the key '" +
                    DictionaryRequestHandler.PARAMETER_KEY_OF_WORD_FOR +
                    "' is absent " + Response.BODY_NEWLINE +
                    "or there is no value for it" + Response.BODY_NEWLINE +
                    "or there is multiple values for it.";


    protected final Dictionary dictionary;
    protected String wordFor;

    public DictionaryRequestHandler(String httpMethodToHandle, List<String> uriPathsToHandle, Dictionary dictionary) {
        super(httpMethodToHandle, uriPathsToHandle);
        this.dictionary = dictionary;
        wordFor = null;
    }

    @Override
    public abstract boolean process(Request request, Response response);

    public boolean checkRequestAndPrepareResponse(Request request, Response response) {
        wordFor = null;
        response.setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_PLAIN_TEXT);

        if (request.getUriParameters().isEmpty()) {
            prepareResponseIfBadWordParameter(response);
            return false;
        }

        List<String> parameterValues = request.getUriParameters().get(PARAMETER_KEY_OF_WORD_FOR);
        if (parameterValues == null || parameterValues.size() != 1) {
            prepareResponseIfBadWordParameter(response);
            return false;
        }

        wordFor = parameterValues.get(0);
        if (wordFor.isEmpty()) {
            prepareResponseIfBadWordParameter(response);
            return false;
        }

        return true;
    }

    public String getWordFor() {
        return wordFor;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    private void prepareResponseIfBadWordParameter(Response response) {
        final String newline = Response.BODY_NEWLINE;
        response.setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        response.setTextBody(MESSAGE_IF_BAD_WORD_PARAMETER);
    }

}
