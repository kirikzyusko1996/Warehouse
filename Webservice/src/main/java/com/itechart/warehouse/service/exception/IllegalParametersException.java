package com.itechart.warehouse.service.exception;

/**
 * Exception thrown on service layer when provided to service methods parameters have illegal value.
 */
public class IllegalParametersException extends Exception {
    public IllegalParametersException() {
    }

    public IllegalParametersException(String message) {
        super(message);
    }

    public IllegalParametersException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalParametersException(Throwable cause) {
        super(cause);
    }
}
