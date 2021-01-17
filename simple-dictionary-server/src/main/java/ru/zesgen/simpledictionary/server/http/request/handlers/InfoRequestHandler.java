package ru.zesgen.simpledictionary.server.http.request.handlers;

import ru.zesgen.simpledictionary.server.http.request.Request;
import ru.zesgen.simpledictionary.server.http.response.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InfoRequestHandler extends RequestHandler {

    public static final String METHOD_TO_HANDLE = "GET";
    public static final String URI_PATH_TO_HANDLE1 = "/dictionary";
    public static final String URI_PATH_TO_HANDLE2 = "/";
    public static final String INFO_FILE_PATH = "info.html";

    public static final List<String> PATHS_TO_HANDLE;

    static {
        List<String> paths = new LinkedList<>();
        paths.add(URI_PATH_TO_HANDLE1);
        paths.add(URI_PATH_TO_HANDLE2);
        PATHS_TO_HANDLE = Collections.unmodifiableList(paths);
    }

    private final ClassLoader classLoader;

    public InfoRequestHandler(ClassLoader classLoader) {
        super(METHOD_TO_HANDLE, PATHS_TO_HANDLE);

        this.classLoader = classLoader;
    }

    @Override
    public boolean process(Request request, Response response) {
        if (!checkCompliance(request)) {
            return false;
        }
        String fileLines;
        InputStream inputStream = classLoader.getResourceAsStream(INFO_FILE_PATH);
        if (inputStream == null) {
            throw new RuntimeException("Can't find the file in the application resources: '" +
                    INFO_FILE_PATH + "`.");
        }
        fileLines = (new BufferedReader(new InputStreamReader(inputStream))).lines()
                .collect(Collectors.joining(Response.BODY_NEWLINE));

        response.setResponseCode(HttpURLConnection.HTTP_OK);
        response.setContentType(Response.CONTENT_TYPE_HEADER_VALUE_FOR_HTML);
        response.setTextBody(fileLines);

        return true;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
