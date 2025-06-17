package it.polimi.ingsw.model.state;

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
