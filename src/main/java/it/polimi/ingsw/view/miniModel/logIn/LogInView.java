package it.polimi.ingsw.view.miniModel.logIn;

/**
 * View class for the login screen in the TUI (Text User Interface).
 */
public class LogInView {

    /**
     * Returns the number of rows to draw for the login view.
     *
     * @return the number of rows to draw
     */
    public static int getRowsToDraw() {
        return 7;
    }

    /**
     * Returns the string to be drawn for a specific line in the login TUI.
     *
     * @param line the line number to draw
     * @return the string representation of the specified line
     * @throws IllegalStateException if the line number is not valid
     */
    public String drawLineTui(int line) {
        return switch (line) {
            case 0 -> "  ___   __   __     __   _  _  _  _    ____  ____  _  _   ___  __ _  ____  ____  ";
            case 1 -> " / __) / _\\ (  )   / _\\ ( \\/ )( \\/ )  (_  _)(  _ \\/ )( \\ / __)(  / )(  __)(  _ \\ ";
            case 2 -> "( (_ \\/    \\/ (_/\\/    \\ )  (  )  /     )(   )   /) \\/ (( (__  )  (  ) _)  )   / ";
            case 3 -> " \\___/\\_/\\_/\\____/\\_/\\_/(_/\\_)(__/     (__) (__\\_)\\____/ \\___)(__\\_)(____)(__\\_) ";
            case 4, 6 -> "\n";
            case 5 -> "Welcome to Galaxy Trucker!!";
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }
}
