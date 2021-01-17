package ru.zesgen.simpledictionary.server.utility;

import java.util.OptionalInt;

public interface UserInputParser {
    OptionalInt parseStringAsTcpPort(String inputString);
}
