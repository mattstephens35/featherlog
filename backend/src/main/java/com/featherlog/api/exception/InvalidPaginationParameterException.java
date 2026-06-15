package com.featherlog.api.exception;

public class InvalidPaginationParameterException extends RuntimeException {

    public InvalidPaginationParameterException(String message) {
        super(message);
    }
}
