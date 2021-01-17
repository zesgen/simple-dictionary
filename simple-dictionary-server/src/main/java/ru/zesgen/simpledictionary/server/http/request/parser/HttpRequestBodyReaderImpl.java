package ru.zesgen.simpledictionary.server.http.request.parser;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class HttpRequestBodyReaderImpl implements HttpRequestBodyReader {

    @Override
    public List<String> readAsTextLines(HttpExchange httpExchange) {
        InputStream is = httpExchange.getRequestBody();
        BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        return in.lines().map(String::trim).collect(Collectors.toList());
    }

}
