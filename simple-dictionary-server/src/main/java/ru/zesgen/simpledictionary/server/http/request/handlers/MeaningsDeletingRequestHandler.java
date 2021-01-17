package ru.zesgen.simpledictionary.server.http.request.handlers;

import ru.zesgen.simpledictionary.server.dao.Dictionary;
import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MeaningsDeletingRequestHandler extends DictionaryRequestHandler {

    public static final String METHOD_TO_HANDLE = "DELETE";
    public static final String URI_PATH_TO_HANDLE = "/dictionary/meanings";
    public static final String MESSAGE_IF_DELETING_IS_SUCCESSFUL =
            "Meaning(s) of the word has(have) been successfully deleted from the dictionary if they was(were).";
    public static final String MESSAGE_IF_NO_MEANING_PROVIDED =
            "Meanings of the word were not provided.";
    public static final String MESSAGE_IF_DELETING_FAILED =
            "Meanings deleting failed. The given word or its meanings are not in the dictionary.";
    public static final List<String> PATHS_TO_HANDLE = Collections.singletonList(URI_PATH_TO_HANDLE);

    public MeaningsDeletingRequestHandler(Dictionary dictionary) {
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

        if (meanings.isEmpty()) {
            prepareResponseNoMeaningsSent(response);
            return true;
        }

        if (dictionary.deleteMeanings(wordFor, meanings)) {
            prepareResponseOk(response);
        } else {
            prepareResponseDeletingFailed(response);
        }

        return true;
    }

    private void prepareResponseOk(Response response) {
        response.setResponseCode(HttpURLConnection.HTTP_OK);
        response.setTextBody(MESSAGE_IF_DELETING_IS_SUCCESSFUL);
    }

    private void prepareResponseNoMeaningsSent(Response response) {
        response.setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        response.setTextBody(MESSAGE_IF_NO_MEANING_PROVIDED);
    }

    private void prepareResponseDeletingFailed(Response response) {
        response.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
        response.setTextBody(MESSAGE_IF_DELETING_FAILED);
    }
}
