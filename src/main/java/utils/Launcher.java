package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {

    /**
     * {@link Environment} static environment variable to define the environment in which the application is running.
     */
    private static Environment environment;

    /**
     * Static string to define the name of the application.
     * It is used to create the folder where the game files are stored.
     */
    private static final String name = "GalaxyTrucker";

    /**
     * Enumeration to define the environment in which the application is running.
     */
    private enum Environment {
        WINDOWS,
        MAC,
        LINUX
    }

    /**
     * Constructor to initialize the environment variable based on the operating system.
     * It checks the system property "os.name" to determine the OS and sets the environment variable accordingly.
     */
    public Launcher() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            environment = Environment.WINDOWS;
        } else if (os.contains("mac")) {
            environment = Environment.MAC;
        } else {
            environment = Environment.LINUX;
        }
    }

    /**
     * Static method to get the data folder path based on the operating system.
     * It creates the folder if it doesn't exist and returns the path.
     *
     * @return {@link Path} to the data folder
     * @throws IOException if an I/O error occurs
     */
    public static Path getDataFolder() throws IOException {
        Path path = switch (environment) {
            case WINDOWS -> Paths.get(System.getenv("APPDATA"), name);
            case MAC -> Paths.get(System.getProperty("user.home"), "Library", "Application Support", name);
            case LINUX -> Paths.get(System.getProperty("user.home"), "." + name);
        };

        // Create the directory if it doesn't exist
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return path;
    }
}
