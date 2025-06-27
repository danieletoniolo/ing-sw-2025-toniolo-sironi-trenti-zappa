package it.polimi.ingsw.view.tui.input;

/**
 * Exception thrown to indicate that the screen has changed in the TUI.
 */
public class ScreenChanged extends RuntimeException {

    /**
     * Constructs a new ScreenChanged exception
     */
    public ScreenChanged() {
        super();
    }


    /**
     * Constructs a new ScreenChanged exception with the specified detail message.
     * @param message the detail message
     */
    public ScreenChanged(String message) {
        super(message);
    }

    /**
     * Constructs a new ScreenChanged exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ScreenChanged(String message, Throwable cause) {
        super(message, cause);
    }
}
