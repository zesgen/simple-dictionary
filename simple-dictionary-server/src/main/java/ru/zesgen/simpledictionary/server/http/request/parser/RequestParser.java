package ru.zesgen.simpledictionary.server.http.request.parser;

import com.sun.net.httpserver.HttpExchange;
import ru.zesgen.simpledictionary.server.http.request.Request;

public interface RequestParser {

    Request parse(HttpExchange httpExchange);
}
