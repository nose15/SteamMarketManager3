package com.manager.httpCommunication;

public class RequestException extends Exception {
    RequestException(Exception e) {
        super(e);
    }

    RequestException(String message) {
        super(message);
    }
}
