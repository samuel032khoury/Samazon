package com.samazon.application.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
