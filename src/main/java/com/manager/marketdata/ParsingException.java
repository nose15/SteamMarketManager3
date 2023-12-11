package com.manager.marketdata;

public class ParsingException extends Exception {
    ParsingException(Exception e) {
        super(e);
    }

    ParsingException(String message) {
        super(message);
    }
}
