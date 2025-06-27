package it.polimi.ingsw.model.state.exception;

/**
 * Exception thrown when there is an error in the synchronous state management.
 * This runtime exception is used to signal issues that occur during
 * synchronous operations in the state management system.
 * @author Daniele Toniolo
 */
public class SynchronousStateException extends RuntimeException {
    /**
     * Constructs a new SynchronousStateException.
     */
    public SynchronousStateException() {
        super();
    }

    /**
     * Constructs a new SynchronousStateException with the specified detail message.
     * @param message the detail message
     */
    public SynchronousStateException(String message) {
        super(message);
    }

    /**
     * Constructs a new SynchronousStateException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public SynchronousStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
