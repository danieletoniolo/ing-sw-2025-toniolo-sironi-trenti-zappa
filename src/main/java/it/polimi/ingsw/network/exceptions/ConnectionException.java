package it.polimi.ingsw.network.exceptions;

/**
 * Exception thrown when a connection-related error occurs.
 * This exception extends RuntimeException and provides various constructors
 * to handle different types of connection failures.
 */
public class ConnectionException extends RuntimeException {
    /**
     * Constructs a new ConnectionException.
     */
    public ConnectionException() {
        super();
    }

    /**
     * Constructs a new ConnectionException with the specified detail message.
     * @param message String of the detail message
     */
    public ConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConnectionException with the specified detail message and cause.
     * @param message String of the detail message
     * @param cause Throwable of the cause that caused this exception
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
