package com.example.clientapi.exception;

/**
 * Exception levée lorsqu'un email existe déjà en base de données.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}