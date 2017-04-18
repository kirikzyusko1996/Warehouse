package exceptions;

public class GenericDAOException extends Exception {
    public GenericDAOException(Exception e) {
        super("The exception occurred in DAO", e, false, false);
    }

    public GenericDAOException(String message, Exception e) {
        super(message, e, false, false);
    }
}