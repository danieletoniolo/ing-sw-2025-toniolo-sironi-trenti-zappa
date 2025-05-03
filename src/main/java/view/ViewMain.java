package view;

import view.tui.TerminalUtils;
import view.tui.TuiManager;
import view.tui.input.Command;
import view.tui.input.Parser;

enum StartCommands {
    TUI,
    GUI,
    EXIT,
    HELP;

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
        System.out.println("""
                     ________  ________  ___       ________     ___    ___ ___    ___      _________  ________  ___  ___  ________  ___  __    _______   ________    \s
                    |\\   ____\\|\\   __  \\|\\  \\     |\\   __  \\   |\\  \\  /  /|\\  \\  /  /|    |\\___   ___\\\\   __  \\|\\  \\|\\  \\|\\   ____\\|\\  \\|\\  \\ |\\  ___ \\ |\\   __  \\   \s
                    \\ \\  \\___|\\ \\  \\|\\  \\ \\  \\    \\ \\  \\|\\  \\  \\ \\  \\/  / | \\  \\/  / /    \\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\\\\\  \\ \\  \\___|\\ \\  \\/  /|\\ \\   __/|\\ \\  \\|\\  \\  \s
                     \\ \\  \\  __\\ \\   __  \\ \\  \\    \\ \\   __  \\  \\ \\    / / \\ \\    / /          \\ \\  \\ \\ \\   _  _\\ \\  \\\\\\  \\ \\  \\    \\ \\   ___  \\ \\  \\_|/_\\ \\   _  _\\ \s
                      \\ \\  \\|\\  \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\  /     \\/   \\/  /  /            \\ \\  \\ \\ \\  \\\\  \\\\ \\  \\\\\\  \\ \\  \\____\\ \\  \\\\ \\  \\ \\  \\_|\\ \\ \\  \\\\  \\|\s
                       \\ \\_______\\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\\/  /\\   \\ __/  / /               \\ \\__\\ \\ \\__\\\\ _\\\\ \\_______\\ \\_______\\ \\__\\\\ \\__\\ \\_______\\ \\__\\\\ _\\\s
                        \\|_______|\\|__|\\|__|\\|_______|\\|__|\\|__/__/ /\\ __\\\\___/ /                 \\|__|  \\|__|\\|__|\\|_______|\\|_______|\\|__| \\|__|\\|_______|\\|__|\\|__|
                                                               |__|/ \\|__\\|___|/                                                                                     \s
                    
                    """);

        while (true) {
            System.out.println("Lost? Type 'HELP' to get a rundown of all commands and what they do.");
            System.out.println();
            System.out.println("To start select the mode: 'Tui' or 'Gui', or 'Exit' to exit.");

            Command command = parser.readCommand();
            StartCommands startCommand = StartCommands.from(command.name());

            if (startCommand == null) {
                System.out.println("Not a valid command. Please try again.");
                continue;
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
                case EXIT:
                    System.out.println("Exiting game...");
                    parser.closeScanner();
                    System.exit(0);
                    break;
                case HELP:
                    System.out.println("To start the game, type 'tui' or 'gui'. To exit, type 'exit'.");
                    break;
            }
        }
    }
}
