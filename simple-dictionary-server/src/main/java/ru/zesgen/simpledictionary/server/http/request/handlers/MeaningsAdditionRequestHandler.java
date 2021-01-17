package ru.zesgen.simpledictionary.server.http.request.handlers;

import ru.zesgen.simpledictionary.server.dao.Dictionary;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MeaningsAdditionRequestHandler extends DictionaryRequestHandler {

    public static final String METHOD_TO_HANDLE = "PUT";
    public static final String URI_PATH_TO_HANDLE = "/dictionary/meanings";
    public static final List<String> PATHS_TO_HANDLE = Collections.singletonList(URI_PATH_TO_HANDLE);

    public static final String MESSAGE_IF_ADDITION_IS_SUCCESSFUL =
            "Meaning(s) of the word has(have) been successfully added to the dictionary if they was(were) provided.";

    public MeaningsAdditionRequestHandler(Dictionary dictionary) {
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

        List<String> meaningList = request.getBodyTextLines();
        Set<String> meanings = new LinkedHashSet<>();
        meaningList.forEach(meaning -> {
            if (!meaning.isEmpty()) {
                meanings.add(meaning);
            }
        });

        if (!meanings.isEmpty()) {
            dictionary.add(wordFor, meanings);
        }

        prepareResponseIfAdded(response);

        return true;
    }

    private void prepareResponseIfAdded(Response response) {
        response.setResponseCode(HttpURLConnection.HTTP_OK);
        response.setTextBody(MESSAGE_IF_ADDITION_IS_SUCCESSFUL);
    }
}
