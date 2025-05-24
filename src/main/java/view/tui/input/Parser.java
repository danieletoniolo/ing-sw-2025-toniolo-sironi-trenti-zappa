package view.tui.input;

import org.javatuples.Pair;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class Parser {
    private int selected;
    private Terminal terminal;
    private LineReader reader;

    public Parser(Terminal terminal) {
        this.terminal = terminal;
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    /**
     * Displays a menu and waits for user input.
     *
     * @param options       The list of options to display.
     * @param menuStartRow  The starting row for the menu display.
     * @return A Command object representing the selected option and its arguments.
     * @throws Exception If an error occurs during input handling.
     */
    public int getCommand(ArrayList<String> options, int menuStartRow) throws Exception {
        terminal.reader().skip(terminal.reader().available());
        terminal.enterRawMode();
        menuStartRow += 2;
        var reader = terminal.reader();
        var writer = terminal.writer();
        selected = 0;

        boolean reading = true;
        while (reading) {
            renderMenu(writer, options, menuStartRow);

            int key = reader.read();

            switch (key) {
                case 'w':
                    selected = (selected - 1 + options.size()) % options.size();
                    break;
                case 's':
                    selected = (selected + 1) % options.size();
                    break;
                case 10, 13: // Select
                    terminal.writer().println("\nYou chose: " + options.get(selected));
                    terminal.flush();
                    reading = false;
                    break;
            }
        }

        return selected;
    }

    public Pair<Integer, Integer> getRowAndCol(String input, int menuStartRow) {
        menuStartRow += 2;

        while (true) {
            terminal.writer().printf("\033[%d;0H", menuStartRow);
            terminal.writer().print("\033[2K");
            terminal.writer().print(input);
            terminal.flush();

            try {
                String line = reader.readLine("");

                String[] parts = line.trim().split("\\s+");
                if (parts.length != 2) {
                    terminal.writer().printf("\033[%d;0H", menuStartRow + 1);
                    terminal.writer().print("\033[2K");
                    terminal.writer().print("Input is not valid, type 'row col' (ex: 10 5)");
                    terminal.flush();
                    continue;
                }

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);

                terminal.writer().printf("\033[%d;0H", menuStartRow + 1);
                terminal.writer().print("\033[2K");
                terminal.flush();

                restoreRawMode();
                return new Pair<>(x, y);

            } catch (Exception e) {
                terminal.writer().printf("\033[%d;0H", menuStartRow + 1);
                terminal.writer().print("\033[2K");
                terminal.writer().print("Input is not valid, type 'row col' (ex: 10 5)");
                terminal.flush();
            }
        }
    }

    public String readNickname(String prompt, int menuStartRow) {
        menuStartRow += 2;
        while (true) {
            terminal.writer().printf("\033[%d;0H", menuStartRow);
            terminal.writer().print("\033[2K");
            terminal.writer().print(prompt);
            terminal.flush();

            try {
                String input = reader.readLine("").trim();

                if (!input.matches("^[a-zA-Z0-9]+$")) {
                    terminal.writer().printf("\033[%d;0H", menuStartRow + 1);
                    terminal.writer().print("\033[2K");
                    terminal.writer().print("Invalid input. Use only letters and numbers, no spaces.");
                    terminal.flush();
                    continue;
                }

                terminal.writer().printf("\033[%d;0H", menuStartRow + 1);
                terminal.writer().print("\033[2K");
                terminal.flush();

                restoreRawMode();
                return input;

            } catch (UserInterruptException | EndOfFileException e) {
                terminal.writer().printf("\033[%d;0H", menuStartRow + 1);
                terminal.writer().print("\033[2K");
                terminal.writer().print("Input aborted.");
                terminal.flush();
            }
        }
    }

    private void restoreRawMode() {
        try {
            terminal.enterRawMode();
            terminal.writer().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Renders the menu options to the terminal.
     *
     * @param writer        The PrintWriter to write to the terminal.
     * @param options       The list of options to display.
     * @param menuStartRow  The starting row for the menu display.
     */
    private void renderMenu(java.io.PrintWriter writer, ArrayList<String> options, int menuStartRow) {
        for (int i = 0; i < options.size(); i++) {
            int row = menuStartRow + i;
            writer.printf("\033[%d;0H", row);
            writer.print("\033[2K");
            if (i == selected) {
                writer.print("> " + options.get(i));
            } else {
                writer.print("  " + options.get(i));
            }
        }
        writer.flush();
    }
}
