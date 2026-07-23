package org.example.backend.common.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException{
    private final String err_code;
    private String message;

    public DuplicateResourceException(String err_code, String message) {
        super(message);
        this.err_code = err_code;
    }
}
