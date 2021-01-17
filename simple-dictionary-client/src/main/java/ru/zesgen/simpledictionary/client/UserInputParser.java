package ru.zesgen.simpledictionary.client;

public interface UserInputParser {

    Request parseAsRequestOrNull(String[] args);

}
