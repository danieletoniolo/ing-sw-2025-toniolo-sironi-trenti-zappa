package it.polimi.ingsw.utils;

import org.javatuples.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Logger class to log messages to a file and/or console.
 * <p>
 * This class implements the Singleton pattern to ensure that only one instance of the logger exists.
 * It provides methods to log messages with different {@link LogLevel} (INFO, WARNING, ERROR) and
 * allows configuring whether to write logs to a file or console.
 * </p>
 * The logger creates a new log file with a timestamp in the name every time it is instantiated and
 * cleans up old log files to keep only the 10 most recent ones (including the current one).
 * <p>
 * To optimize performance, the logger uses a separate thread to print log messages to the file and/or console.
 * This allows the main thread to continue executing without waiting for the logging operation to complete.
 * @author Daniele Toniolo
 */
public class Logger {

    /**
     * {@link Logger} static instance to implement the Singleton pattern.
     */
    private static Logger logger;

    /**
     * {@link Path} to the folder where log files are stored.
     */
    private final Path logsFolder;

    /**
     * Log levels for the logger to categorize messages.
     * <ul>
     *     <li>INFO: Informational messages</li>
     *     <li>WARNING: Warning messages</li>
     *     <li>ERROR: Error messages</li>
     * </ul>
     */
    public enum LogLevel {
        INFO("INFO"),
        WARNING("WARNING"),
        ERROR("ERROR"),
        DEBUG("DEBUG");

        private final String level;

        LogLevel(String level) {
            this.level = level;
        }

        @Override
        public String toString() {
            return "[" + this.level + "]";
        }
    }

    /**
     * Sets whether to write logs to a file.
     * <ul>
     *     <li>true: logs will be written to a file</li>
     *     <li>false: logs will not be written to a file</li>
     * </ul>
     */
    private final boolean writeToFile;

    /**
     * Sets whether to write logs to the console.
     * <ul>
     *     <li>true: logs will be written to the console</li>
     *     <li>false: logs will not be written to the console</li>
     * </ul>
     */
    private final boolean writeToConsole;

    /**
     * List of messages to log.
     */
    private final Queue<Pair<LogLevel, String>> messageQueue;

    /**
     * Current log file name. It is created every time the Logger is instantiated.
     */
    private static final String logFolder = "logs";

    /**
     * ANSI color code for red text output in console.
     */
    private static final String RED = "\u001B[31m";

    /**
     * ANSI color code for yellow text output in console.
     */
    private static final String YELLOW = "\u001B[33m";

    /**
     * ANSI color code for blue text output in console.
     */
    private static final String BLUE = "\u001B[34m";

    /**
     * ANSI color code for green text output in console.
     */
    private static final String GREEN = "\u001B[32m";

    /**
     * ANSI reset code to restore default text color in console.
     */
    private static final String RESET = "\u001B[0m";

    /**
     * The log file name.
     */
    private final String logFileName;

    /**
     * The background thread that processes log messages from the queue.
     * This thread runs continuously and waits for messages to be added to the message queue,
     * then writes them to the file and/or console based on the logger configuration.
     */
    private final Thread logThread;

    /**
     * Private constructor to prevent instantiation and ensure Singleton pattern.
     * Initializes the log folder, file name and starts the logging thread.
     * @throws IOException if an I/O error occurs
     */
    private Logger(boolean writeToConsole, boolean writeToFile) throws IOException {
        // Set whether to write logs to console and file
        this.writeToConsole = writeToConsole;
        this.writeToFile = writeToFile;

        // Initialize the message list
        messageQueue = new LinkedList<>();

        logsFolder = Paths.get(Launcher.getDataFolder().toString(), logFolder);

        // Create the file log
        Files.createDirectories(logsFolder);
        // Create the log file name
        logFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".txt";

        // Clean up old log files
        cleanUp();

        // Start the log thread
        logThread = new Thread(() -> {
            while (true) {
                Pair<LogLevel, String> message;
                synchronized (messageQueue) {
                    // Wait for a message to be added to the queue
                    while (messageQueue.isEmpty()) {
                        try {
                            messageQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // Get the message from the queue
                    message = messageQueue.poll();
                }
                // Log the message
                String logMessage = message.getValue1();
                LogLevel level = message.getValue0();
                if (this.writeToFile) {
                    try {
                        // Create the log file if it doesn't exist
                        Path logFile = logsFolder.resolve(logFileName);
                        Files.write(logFile, logMessage.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    } catch (IOException e) {
                        System.err.println("Error writing to log file: " + e.getMessage());
                    }
                }
                if (this.writeToConsole) {
                    logMessage = switch (level) {
                        case INFO -> BLUE + logMessage + RESET;
                        case WARNING -> YELLOW + logMessage + RESET;
                        case ERROR -> RED + logMessage + RESET;
                        case DEBUG -> GREEN + logMessage + RESET;
                    };
                    System.out.print(logMessage);
                }
            }
        });
        logThread.start();
        // Print the starting message
        logInfo("Starting logger and cleaning up old log files", false);
        logInfo("Current log file: " + logsFolder + logFileName, false);
    }


    /**
     * Logs a message with the specified log level and time stamp and prints the caller function name if specified.
     * @param level the log level (INFO, WARNING, ERROR)
     * @param message the message to log
     * @param printCallerFunction whether to print the caller function name
     */
    public void log(LogLevel level, String message, boolean printCallerFunction) {
        String logMessage = formatString(level, message, printCallerFunction);

        // Add the message to the queue
        synchronized (messageQueue) {
            messageQueue.add(new Pair<>(level, logMessage));
            messageQueue.notifyAll();
        }
    }

    /**
     * Logs an informational message with the INFO log level and prints the caller function name if specified.
     * @param message the message to log
     * @param printCallerFunction whether to print the caller function name
     */
    public void logInfo(String message, boolean printCallerFunction) {
        log(LogLevel.INFO, message, printCallerFunction);
    }

    /**
     * Logs a warning message with the WARNING log level and prints the caller function name if specified.
     * @param message the warning message to log
     * @param printCallerFunction whether to print the caller function name
     */
    public void logWarning(String message, boolean printCallerFunction) {
        log(LogLevel.WARNING, message, printCallerFunction);
    }

    /**
     * Logs a warning message with the WARNING log level and prints the caller function name if specified.
     * @param message the error message to log
     * @param printCallerFunction whether to print the caller function name
     */
    public void logError(String message, boolean printCallerFunction) {
        log(LogLevel.ERROR, message, printCallerFunction);
    }

    /**
     * Logs a debug message with the DEBUG log level and prints the caller function name if specified.
     * @param message the message to log
     */
    public void logDebug(String message, boolean printCallerFunction) {
        log(LogLevel.DEBUG, message, printCallerFunction);
    }

    /**
     * Formats the log message with the specified log level and time stamp and optionally prints the caller function name.
     * @param level the log level (INFO, WARNING, ERROR).
     * @param message the message to log.
     * @param printCallerFunction whether to print the caller function name (true) or not (false).
     * @return the formatted log message.
     */
    private static String formatString(LogLevel level, String message, boolean printCallerFunction) {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("[%-19s] %-10s %s", timeStamp, level.toString(), message);
        if (printCallerFunction) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String callerClassName = stackTrace[4].toString();
            logMessage += " - " + callerClassName;
            logMessage += " [Thread: " + Thread.currentThread().getName() + " - " + Thread.currentThread().threadId() + "]";
        }
        logMessage += System.lineSeparator();
        return logMessage;
    }

    /**
     * Cleans up old log files to maintain a maximum of 10 log files (including the current one).
     * <p>
     * This method scans the logs folder for .txt files, sorts them by last modified date
     * (newest first), and deletes all files except the 9 most recent ones. This ensures
     * that the total number of log files never exceeds 10, preventing unlimited disk usage.
     * </p>
     * If an error occurs during the cleanup process, it is logged to stderr and the
     * application exits with status code 1, as this is considered a critical error.
     */
    private void cleanUp() {
        // Clean up old log files
        try {
            Files.list(logsFolder)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .sorted((p1, p2) -> Long.compare(p2.toFile().lastModified(), p1.toFile().lastModified()))
                    .skip(9) // Keep the 9 most recent log files
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Error deleting log file: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            // Critical error, log it and exit
            System.err.println("Error cleaning up log files: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Returns the singleton instance of the Logger class.
     * If the logger is not initialized, it creates a new instance with default settings.
     * @return the singleton instance of Logger
     */
    public static Logger getInstance() {
        if (logger == null) {
            try {
                logger = new Logger(true, true);
            } catch (IOException e) {
                // Critical error, log it and exit
                System.err.println("Error initializing logger: " + e.getMessage());
                System.exit(1);
            }
        }
        return logger;
    }

    /**
     * Returns the singleton instance of the Logger class.
     * If the logger is not initialized, it creates a new instance with the specified settings.
     * @return the singleton instance of Logger
     */
    public static Logger getInstance(boolean writeToConsole, boolean writeToFile) {
        if (logger == null) {
            try {
                logger = new Logger(writeToConsole, writeToFile);
            } catch (IOException e) {
                // Critical error, log it and exit
                System.err.println("Error initializing logger: " + e.getMessage());
                System.exit(1);
            }
        }
        return logger;
    }

    /**
     * Shuts down the logger and stops the logging thread.
     */
    public void shutdown() {
        // Stop the logger thread
        synchronized (messageQueue) {
            logThread.interrupt();
            messageQueue.notifyAll();
        }
    }
}
