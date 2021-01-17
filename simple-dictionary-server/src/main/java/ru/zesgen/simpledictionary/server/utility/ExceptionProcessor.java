package ru.zesgen.simpledictionary.server.utility;

public interface ExceptionProcessor {

    String extractStackTrace(Throwable exception);

}
