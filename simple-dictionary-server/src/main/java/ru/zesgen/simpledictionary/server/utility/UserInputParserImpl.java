package ru.zesgen.simpledictionary.server.utility;

import java.util.OptionalInt;

public class UserInputParserImpl implements UserInputParser {
    @Override
    public OptionalInt parseStringAsTcpPort(String inputString) {
        OptionalInt optionalPort = OptionalInt.empty();
        try {
            int port = Integer.parseInt(inputString.trim());
            if (port > 0 && port < 65536) {
                optionalPort = OptionalInt.of(port);
            }
        } catch (NumberFormatException e) {
            /* Do nothing*/
        }
        return optionalPort;
    }
}
