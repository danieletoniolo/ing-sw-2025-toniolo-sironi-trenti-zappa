package view;

import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class TestJline {
    private static final String[] options = {"Gioca", "Esci"};
    private static int selected = 0;

    public static void main(String[] args) throws IOException {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .jna(true)
                    .jansi(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Errore nella creazione del terminale: " + e.getMessage());
            return;
        }

        terminal.enterRawMode(); // Legge i tasti direttamente
        var reader = terminal.reader();
        var writer = terminal.writer();

        while (true) {
            renderMenu(writer);
            int key = reader.read();
            writer.println("ciao");

            switch (key) {
                case 'w':
                    selected = (selected - 1 + options.length) % options.length;
                    break;
                case 's':
                    selected = (selected + 1) % options.length;
                    break;
                case 10, 13: // Invio
                    terminal.writer().println("\nHai scelto: " + options[selected]);
                    terminal.flush();
                    System.exit(0);
            }
        }
    }

    private static void renderMenu(java.io.PrintWriter writer) {
        writer.print("\033[H\033[2J"); // Pulisce il terminale
        writer.flush();
        writer.println("=== MENU ===");
        for (int i = 0; i < options.length; i++) {
            if (i == selected) {
                writer.println("> " + options[i]);
            } else {
                writer.println("  " + options[i]);
            }
        }
        writer.flush();
    }
}
