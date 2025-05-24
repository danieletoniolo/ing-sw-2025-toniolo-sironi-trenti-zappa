package it.polimi.ingsw.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {
    /**
     * Static string to define the name of the application.
     * It is used to create the folder where the game files are stored.
     */
    private static final String name = "GalaxyTrucker";

    /**
     * Static method to get the data folder path based on the operating system.
     * It creates the folder if it doesn't exist and returns the path.
     *
     * @return {@link Path} to the data folder
     * @throws IOException if an I/O error occurs
     */
    public static Path getDataFolder() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Path path;
        if (os.contains("win")) {
            path = Paths.get(System.getenv("APPDATA"), name);
        } else if (os.contains("mac")) {
            path = Paths.get(System.getProperty("user.home"), "Library", "Application Support", name);
        } else {
            path = Paths.get(System.getProperty("user.home"), "." + name);
        }

        // Create the directory if it doesn't exist
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return path;
    }

    public static void launchTerminal(String jarToLaunch) throws IOException, URISyntaxException {
        String os = System.getProperty("os.name").toLowerCase();

        // Get the path absolute to the jar file
        Path appDir = Paths.get(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        Path jarPath = appDir.resolve(jarToLaunch);

        // TODO: This will be changed as soon as we have a integrated runtime
        String javaCmd = "java -jar \"" + jarPath + "\"";

        if (os.contains("win")) {
            // start cmd /k "java -jar ..."
            new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", javaCmd).start();

        } else if (os.contains("mac")) {
            String appleScriptCommand = "tell application \"Terminal\" to do script \"" + javaCmd.replace("\"", "\\\"") + "\"";
            new ProcessBuilder("osascript", "-e", appleScriptCommand).start();

        } else if (os.contains("nux")) {
            // Multiple terminal emulators are available on Linux, so we try to launch one of them
            String[] terminals = {
                    "x-terminal-emulator",
                    "gnome-terminal",
                    "konsole",
                    "xfce4-terminal",
                    "xterm"
            };

            boolean launched = false;
            for (String term : terminals) {
                try {
                    new ProcessBuilder(term, "-e", "bash", "-c", javaCmd + "; read").start();
                    launched = true;
                    break;
                } catch (IOException ignored) {}
            }

            if (!launched) {
                Logger.getInstance().log(Logger.LogLevel.ERROR, "No terminal emulator found. Please run the command manually: " + javaCmd, false);
            }

        } else {
            Logger.getInstance().log(Logger.LogLevel.ERROR, "Unknown operating system: " + os, false);
        }
    }
}
