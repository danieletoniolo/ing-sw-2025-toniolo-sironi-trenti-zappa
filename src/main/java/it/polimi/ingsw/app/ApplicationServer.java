package it.polimi.ingsw.app;

import it.polimi.ingsw.utils.Launcher;

/**
 * ApplicationServer is a simple class that serves as the entry point for the server application.
 * It uses the {@link Launcher} class to launch the server in a new terminal window based on the operating system.
 * <p>
 * This class is the one executed after JPackage is executed, so that the user can run the server without
 * having to open a terminal and run the jar file manually.
 * @author Daniele Toniolo
 */
public class ApplicationServer {
    /**
     * Main method that serves as the entry point for the server application.
     * <p>
     * This method launches the server by calling the Launcher utility to open
     * the Server.jar file in a new terminal window. If any exception occurs
     * during the launch process, the application exits with status code 1.
     *
     * @param args command line arguments (not used in this implementation)
     */
    public static void main(String[] args) {
        try {
            // Launch the server jar file in a new terminal window
            Launcher.launchTerminal("Server.jar");
        } catch (Exception e) {
            // Exit with error status if launch fails
            System.exit(1);
        }
    }
}
