package it.polimi.ingsw.network.exceptions;

/**
 * Exception thrown when an invalid port number is provided.
 * This exception extends RuntimeException and is typically used
 * in network-related operations when port validation fails.
 */
public class BadPortException extends RuntimeException {
    /**
     * Constructs a new BadPortException.
     */
    public BadPortException() {
        super();
    }

    /**
     * Constructs a new BadPortException with the specified detail message.
     * @param message String of the detail message
     */
    public BadPortException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadPortException with the specified detail message and cause.
     * @param message String of the detail message
     * @param cause Throwable of the cause that caused this exception
     */
    public BadPortException(String message, Throwable cause) {
        super(message, cause);
    }
}
