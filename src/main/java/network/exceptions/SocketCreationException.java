package network.exceptions;

public class SocketCreationException extends RuntimeException {
    /**
     * Constructs a new SocketCreationException.
     */
    public SocketCreationException() {
        super();
    }

    /**
     * Constructs a new SocketCreationException with the specified detail message.
     * @param message String of the detail message
     */
    public SocketCreationException(String message) {
        super(message);
    }

    /**
     * Constructs a new SocketCreationException with the specified detail message and cause.
     * @param message String of the detail message
     * @param cause Throwable of the cause that caused this exception
     */
    public SocketCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
