package ru.zesgen.simpledictionary.server.utility;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface UriParametersParser {
    Map<String, List<String>> parse(URI uri);
}
