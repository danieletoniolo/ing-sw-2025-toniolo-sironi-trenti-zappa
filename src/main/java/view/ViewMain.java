package view;

import view.tui.TerminalUtils;
import view.tui.TuiManager;
import view.tui.input.Command;
import view.tui.input.Parser;

enum StartCommands {
    TUI,
    GUI,
    QUIT;

    public static StartCommands from(String name) {
        try {
            return StartCommands.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

public class ViewMain {
    public static void main(String[] args) {
        Parser parser = new Parser();
        Command command;
        StartCommands startCommand = null;


        while (true) {
            System.out.println("Select the mode: TUI or GUI, or QUIT to exit.");

            command = parser.readCommand();
            startCommand = StartCommands.from(command.name());

            if (startCommand == null) {
                System.out.println("Not a valid command. Please try again.");
            }
            else {
                break;
            }
        }

        switch (startCommand) {
            case TUI:
                System.out.println("You selected: " + command.name());
                TerminalUtils.clearTerminal();
                // Start TUI
                TuiManager tuiManager = new TuiManager();
                tuiManager.startTui();
                break;
            case GUI:
                System.out.println("You selected: " + command.name());
                break;
            case QUIT:
                System.out.println("Quitting game...");
                parser.closeScanner();
                System.exit(0);
                break;
        }

        parser.closeScanner();
    }
}
