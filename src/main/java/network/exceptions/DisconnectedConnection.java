package network.exceptions;

public class DisconnectedConnection extends RuntimeException {
    /**
     * Constructs a new DisconnectedConnection exception.
     */
    public DisconnectedConnection() {
        super();
    }

    /**
     * Constructs a new DisconnectedConnection exception with the specified detail message.
     * @param message String of the detail message
     */
    public DisconnectedConnection(String message) {
        super(message);
    }

    /**
     * Constructs a new DisconnectedConnection exception with the specified detail message and cause.
     * @param message String of the detail message
     * @param cause Throwable of the cause that caused this exception
     */
    public DisconnectedConnection(String message, Throwable cause) {
        super(message, cause);
    }
}
