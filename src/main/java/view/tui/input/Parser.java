package view.tui.input;

import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class Parser {
    private int selected = 0;
    private Terminal terminal;

    public Parser(Terminal terminal) {
        this.terminal = terminal;
    }

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

        String[] commandParts = options.get(selected).split(" ");
        String commandName = commandParts[0];
        String[] commandArgs = new String[commandParts.length - 1];
        System.arraycopy(commandParts, 1, commandArgs, 0, commandParts.length - 1);
        return new Command(commandName, commandArgs);
    }

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
