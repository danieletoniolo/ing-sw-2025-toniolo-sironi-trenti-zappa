package view.tui.input;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class Parser {
    private int selected = 0;
    private Terminal terminal;

    public Parser(Terminal terminal) {
        this.terminal = terminal;
    }

    /**
     * Displays a menu and waits for user input.
     *
     * @param options       The list of options to display.
     * @param menuStartRow  The starting row for the menu display.
     * @return A Command object representing the selected option and its arguments.
     * @throws Exception If an error occurs during input handling.
     */
    public Command getCommand(ArrayList<String> options, int menuStartRow) throws Exception {
        terminal.enterRawMode();
        menuStartRow += 2;
        var reader = terminal.reader();
        var writer = terminal.writer();

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

        String selectedOption = options.get(selected);
        String commandName = selectedOption.split(" ")[0];
        String[] commandArgs;

        // Specific input handling for "pick tile" command
        if (selectedOption.toLowerCase().contains("pick tile") || selectedOption.toLowerCase().contains("put tile on spaceship")) {
            terminal.echo(true);
            terminal.writer().print("Enter coordinates (row,col): ");
            terminal.flush();

            // Crea LineReader per input riga
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            String input = lineReader.readLine();
            commandArgs = new String[]{input.trim()};
        } else {
            String[] commandParts = selectedOption.split(" ");
            commandArgs = new String[commandParts.length - 1];
            System.arraycopy(commandParts, 1, commandArgs, 0, commandParts.length - 1);
        }

        return new Command(commandName, commandArgs);
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
