package com.manager.marketdata;

public class FetchingException extends Exception {
    FetchingException(Exception e) {
        super(e);
    }

    FetchingException(String message) {
        super(message);
    }
}
