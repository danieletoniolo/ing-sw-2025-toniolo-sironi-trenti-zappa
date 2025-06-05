package it.polimi.ingsw.app;

import it.polimi.ingsw.utils.Launcher;

/**
 * ApplicationServer is a simple class that serves as the entry point for the server application.
 * It uses the {@link Launcher} class to launch the server in a new terminal window based on the operating system.
 * <p>
 * This class is the one executed after JPackage is executed, so that the user can run the server without
 * having to open a terminal and run the jar file manually.
 */
public class ApplicationServer {
    public static void main(String[] args) {
        try {
            Launcher.launchTerminal("Server.jar");
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
