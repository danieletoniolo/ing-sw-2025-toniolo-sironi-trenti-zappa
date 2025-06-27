package it.polimi.ingsw.view.tui;

import org.jline.terminal.Terminal;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for terminal operations using JLine.
 * Provides methods to print and update lines in the terminal efficiently,
 * handle terminal resizing, and manage raw mode.
 */
public class TerminalUtils {
    /** Lock object for synchronizing terminal output operations. */
    private static final Object lock = new Object();

    /** The JLine Terminal instance used for output. */
    private static Terminal terminal;

    /** Last known terminal width. */
    private static int lastWidth = -1;

    /** Last known terminal height. */
    private static int lastHeight = -1;

    /** Stores the last printed lines for efficient screen updates. */
    private static List<String> lastPrintedLines = new ArrayList<>();

    /** Stores the last value of afterOptions used in printScreen. */
    private static int lastPrintedAfterOptions;

    /**
     * Sets the terminal instance to be used by this utility class.
     * @param terminal the JLine Terminal instance
     */
    public static void setTerminal(Terminal terminal) {
        TerminalUtils.terminal = terminal;
    }

    /**
     * Prints a single line at the specified row in the terminal.
     * The row is 1-indexed.
     * @param output the string to print
     * @param row the row number (1-indexed)
     */
    public static void printLine(String output, int row) {
        synchronized (lock) {
            terminal.writer().printf("\033[%d;0H", row);
            terminal.writer().print("\033[2K");
            terminal.writer().println(output);
            terminal.writer().flush();
        }
    }

    /**
     * Restores the terminal to raw mode.
     * Handles exceptions by printing the stack trace.
     */
    public static void restoreRawMode() {
        try {
            terminal.enterRawMode();
            terminal.writer().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears all lines in the terminal starting from the specified row.
     * @param startingRow the row from which to start clearing (inclusive)
     */
    public static void clearLastLines(int startingRow) {
        for (int i = startingRow; i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine("", i);
        }
    }

    /**
     * Prints the screen with the new lines, updating only the changed lines.
     * Handles terminal resizing and clears lines after the options if needed.
     * @param newLines the new lines to print
     * @param afterOptions the number of lines to clear after the options
     */
    public static void printScreen(List<String> newLines, int afterOptions) {
        int width = terminal.getWidth();
        int height = terminal.getHeight();
        boolean resized = (width != lastWidth) || (height != lastHeight);
        if (resized) {
            for (int row = 0; row < newLines.size(); row++) {
                TerminalUtils.printLine(newLines.get(row), row + 1); // Rows are 1-indexed in terminal
            }
            TerminalUtils.clearLastLines(afterOptions);
            lastWidth = width;
            lastHeight = height;
        }
        else {
            for (int row = 0; row < newLines.size(); row++) {
                String newLine = newLines.get(row);
                String oldLine = (row < lastPrintedLines.size()) ? lastPrintedLines.get(row) : null;
                if (!newLine.equals(oldLine)) {
                    TerminalUtils.printLine(newLine, row + 1); // Rows are 1-indexed in terminal
                }
            }
            for (int row = afterOptions; row < lastPrintedAfterOptions; row++) {
                TerminalUtils.printLine("", row);
            }
        }

        lastPrintedLines = new ArrayList<>(newLines);
        lastPrintedAfterOptions = afterOptions;
    }
}
