package com.itechart.warehouse.service.exception;

/**
 * Exception thrown on service layer when GenericDAOException get caught.
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
