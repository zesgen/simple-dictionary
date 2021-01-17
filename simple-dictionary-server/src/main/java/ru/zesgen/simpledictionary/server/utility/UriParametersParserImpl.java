package ru.zesgen.simpledictionary.server.utility;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

public class UriParametersParserImpl implements UriParametersParser {

    public static final String SEPARATOR_OF_PARAMETERS_IN_URI_QUERY = "&";

    private final String queryEncoding;

    public UriParametersParserImpl(String queryEncoding) {
        this.queryEncoding = queryEncoding;
    }

    @Override
    public Map<String, List<String>> parse(URI uri) {
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(query.split(SEPARATOR_OF_PARAMETERS_IN_URI_QUERY))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        LinkedHashMap::new, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    private Map.Entry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = (idx > 0) ? it.substring(0, idx) : it;
        final String value = (idx > 0 && it.length() > idx + 1) ? it.substring(idx + 1) : "";
        try {
            String decodedKey = URLDecoder.decode(key, queryEncoding);
            String decodedValue = URLDecoder.decode(value, queryEncoding);
            return new AbstractMap.SimpleImmutableEntry<>(decodedKey, decodedValue);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException during split URI query parameter in the class: " +
                    this.getClass().getName(), e);
        }
    }
}
