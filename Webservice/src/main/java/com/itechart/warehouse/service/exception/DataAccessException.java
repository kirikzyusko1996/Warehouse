package com.itechart.warehouse.service.exception;

/**
 * Created by Aleksandr on 23.04.2017.
 */
public class DataAccessException extends Exception{
    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
