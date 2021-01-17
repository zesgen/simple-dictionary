package ru.zesgen.simpledictionary.server.http.request.handlers;

import ru.zesgen.simpledictionary.server.dao.Dictionary;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MeaningsGettingRequestHandler extends DictionaryRequestHandler {

    public static final String METHOD_TO_HANDLE = "GET";
    public static final String URI_PATH_TO_HANDLE = "/dictionary/meanings";
    public static final List<String> PATHS_TO_HANDLE = Collections.singletonList(URI_PATH_TO_HANDLE);
    public static final String MESSAGE_IF_WORD_IS_ABSENT = "The given word is not in the dictionary: ";

    public MeaningsGettingRequestHandler(Dictionary dictionary) {
        super(METHOD_TO_HANDLE, PATHS_TO_HANDLE, dictionary);
    }

    @Override
    public boolean process(Request request, Response response) {
        if (!checkCompliance(request)) {
            return false;
        }
        if (!checkRequestAndPrepareResponse(request, response)) {
            return true;
        }

        Set<String> meanings = dictionary.get(wordFor);

        if (meanings.isEmpty()) {
            prepareResponseIfGetFailed(response, wordFor);
        } else {
            prepareResponseIfGetSucceeded(response, meanings);
        }

        return true;
    }

    private void prepareResponseIfGetSucceeded(Response response, Set<String> meanings) {
        response.setResponseCode(HttpURLConnection.HTTP_OK);
        response.setTextBody(String.join(Response.BODY_NEWLINE, meanings));
    }

    private void prepareResponseIfGetFailed(Response response, String word) {
        response.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
        response.setTextBody(MESSAGE_IF_WORD_IS_ABSENT + word);
    }
}
