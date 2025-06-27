package it.polimi.ingsw.app;

import it.polimi.ingsw.utils.Launcher;

/**
 * ApplicationClient is a simple class that serves as the entry point for the client application.
 * It uses the {@link Launcher} class to launch the client in a new terminal window based on the operating system.
 * <p>
 * This class is the one executed after JPackage is executed, so that the user can run the client without
 * having to open a terminal and run the jar file manually.
 * @author Daniele Toniolo
 */
public class ApplicationClient {
    /**
     * Main entry point for the client application.
     * <p>
     * Launches the Client.jar file in a new terminal window using the Launcher utility.
     * If any exception occurs during the launch process, the application exits with status code 1.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            Launcher.launchTerminal("Client.jar");
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
