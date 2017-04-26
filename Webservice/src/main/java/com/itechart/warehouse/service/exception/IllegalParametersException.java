package com.itechart.warehouse.service.exception;

/**
 * Created by Aleksandr on 25.04.2017.
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
