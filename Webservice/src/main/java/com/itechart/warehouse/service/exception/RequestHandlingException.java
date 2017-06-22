package com.itechart.warehouse.service.exception;

/**
 * Exception thrown when service or controller did not managed to successfully handle request.
 */
public class RequestHandlingException extends Exception{
    public RequestHandlingException() {
    }

    public RequestHandlingException(String message) {
        super(message);
    }

    public RequestHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestHandlingException(Throwable cause) {
        super(cause);
    }
}
