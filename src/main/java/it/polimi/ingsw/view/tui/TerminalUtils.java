package it.polimi.ingsw.view.tui;

import org.jline.terminal.Terminal;

import java.io.PrintWriter;

public class TerminalUtils {
    public static void printLine(PrintWriter writer, String output, int row) {
        writer.printf("\033[%d;0H", row);
        writer.print("\033[2K");
        writer.println(output);
        writer.flush();
    }

    public static void restoreRawMode(Terminal terminal) {
        try {
            terminal.enterRawMode();
            terminal.writer().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
