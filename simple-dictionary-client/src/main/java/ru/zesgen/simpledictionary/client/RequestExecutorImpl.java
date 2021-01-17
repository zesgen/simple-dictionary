package ru.zesgen.simpledictionary.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RequestExecutorImpl implements RequestExecutor {

    @Override
    public Response execute(Request request) {
        Response response = new Response(false, 0, "");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod(request.getHttpMethod());
            connection.setRequestProperty(Request.HTTP_HEADER_CONTENT_TYPE_KEY,
                    Request.HTTP_HEADER_CONTENT_TYPE_VALUE);
            if (!request.getHttpMethod().equals(Request.HTTP_METHOD_GET)) {
                connection.setDoOutput(true);
                try (OutputStreamWriter out =
                             new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                    out.write(request.getHttpBody());
                }
            }
            connection.connect();
            response.setHttpResponseCode(connection.getResponseCode());
            if (response.getHttpResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader bufferedReader =
                             new BufferedReader(new InputStreamReader(connection.getInputStream(),
                                     StandardCharsets.UTF_8))) {
                    response.setHttpBody(bufferedReader.lines().collect(Collectors.joining("\n")));
                }
            } else {
                try (BufferedReader bufferedReader =
                             new BufferedReader(new InputStreamReader(connection.getErrorStream(),
                                     StandardCharsets.UTF_8))) {
                    response.setHttpBody(bufferedReader.lines().collect(Collectors.joining("\n")));
                }
            }
            response.setConnectionSuccessful(true);
        } catch (Throwable e) {
            /* Do nothing*/
        }
        if (connection != null)
            connection.disconnect();
        return response;
    }
}
