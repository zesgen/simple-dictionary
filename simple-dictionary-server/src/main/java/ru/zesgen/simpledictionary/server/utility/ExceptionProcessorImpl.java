package ru.zesgen.simpledictionary.server.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionProcessorImpl implements ExceptionProcessor {

    @Override
    public String extractStackTrace(Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

}
