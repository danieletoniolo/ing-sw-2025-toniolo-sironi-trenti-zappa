package it.polimi.ingsw.view.tui;

import org.jline.terminal.Terminal;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TerminalUtils {
    private static final Object lock = new Object();
    private static Terminal terminal;
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static List<String> lastPrintedLines = new ArrayList<>();

    public static void setTerminal(Terminal terminal) {
        TerminalUtils.terminal = terminal;
    }

    public static void printLine(String output, int row) {
        synchronized (lock) {
            terminal.writer().printf("\033[%d;0H", row);
            terminal.writer().print("\033[2K");
            terminal.writer().println(output);
            terminal.writer().flush();
        }
    }

    public static void restoreRawMode() {
        try {
            terminal.enterRawMode();
            terminal.writer().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearLastLines(int startingRow) {
        for (int i = startingRow; i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine("", i);
        }
    }

    /**
     * Prints the screen with the new lines, updating only the changed lines.
     * @param newLines the new lines to print
     * @param afterOptions the number of lines to clear after the options
     */
    public static void printScreen(List<String> newLines, int afterOptions) {
        int width = terminal.getWidth();
        int height = terminal.getHeight();
        boolean resized = (width != lastWidth) || (height != lastHeight);
        if (resized) {
            for (int row = 0; row < newLines.size(); row++) {
                TerminalUtils.printLine(newLines.get(row), row + 1);
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
                    TerminalUtils.printLine(newLine, row + 1);
                }
            }
            for (int row = newLines.size() + afterOptions; row < lastPrintedLines.size(); row++) {
                TerminalUtils.printLine("", row + 1);
            }
        }

        lastPrintedLines = new ArrayList<>(newLines);
    }
}
