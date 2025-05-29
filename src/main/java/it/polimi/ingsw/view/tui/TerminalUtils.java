package it.polimi.ingsw.view.tui;

public class TerminalUtils {
    public static void printLine(java.io.PrintWriter writer, String output, int row) {
        writer.printf("\033[%d;0H", row);
        writer.print("\033[2K");
        writer.println(output);
        writer.flush();
    }
}
