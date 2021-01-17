package ru.zesgen.simpledictionary.server.http.request.parser;

import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public interface HttpRequestBodyReader {
    List<String> readAsTextLines(HttpExchange httpExchange);
}
