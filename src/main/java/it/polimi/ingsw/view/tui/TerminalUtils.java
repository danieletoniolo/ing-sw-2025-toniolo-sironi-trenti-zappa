package it.polimi.ingsw.view.tui;

import org.jline.terminal.Terminal;

import java.io.PrintWriter;

public class TerminalUtils {
    private static final Object lock = new Object();

    public static void printLine(PrintWriter writer, String output, int row) {
        synchronized (lock) {
            writer.printf("\033[%d;0H", row);
            writer.print("\033[2K");
            writer.println(output);
            writer.flush();
        }
    }

    public static void restoreRawMode(Terminal terminal) {
        try {
            terminal.enterRawMode();
            terminal.writer().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearLastLines(int startingRow, Terminal terminal) {
        for (int i = startingRow; i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine(terminal.writer(), "", i);
        }
    }
}
