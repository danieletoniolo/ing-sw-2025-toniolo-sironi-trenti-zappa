package it.polimi.ingsw.network.exceptions;

/**
 * Exception thrown when an invalid or unreachable host is encountered.
 * This is a runtime exception that indicates issues with network host resolution or connectivity.
 */
public class BadHostException extends RuntimeException {
    /**
     * Constructs a new BadHostException.
     */
    public BadHostException() {
        super();
    }

    /**
     * Constructs a new BadHostException with the specified detail message.
     * @param message String of the detail message
     */
    public BadHostException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadHostException with the specified detail message and cause.
     * @param message String of the detail message
     * @param cause Throwable of the cause that caused this exception
     */
    public BadHostException(String message, Throwable cause) {
        super(message, cause);
    }
}
