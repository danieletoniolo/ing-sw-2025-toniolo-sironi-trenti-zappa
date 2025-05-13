package view.tui.states;

import view.tui.TerminalUtils;
import view.tui.input.Command;

import java.util.ArrayList;

public class StartingState implements StateView {
    ArrayList<String> validCommands;

    public StartingState() {
        validCommands = new ArrayList<>();
        validCommands.add("tui");
        validCommands.add("gui");
        validCommands.add("exit");
        validCommands.add("help");
    }

    @Override
    public StateView isValidCommand(Command command) {
        if (validCommands.contains(command.name())) {
            throw new IllegalStateException("Invalid command: " + command.name());
        }

        switch (command.name()) {
            case "tui":
                System.out.println("You selected: " + command.name());
                TerminalUtils.clearTerminal();
                return new StartingState();
        }
        throw new IllegalStateException("Invalid command: " + command.name());
    }

    @Override
    public void printTui() {
        System.out.println("Lost? Type 'HELP' to get a rundown of all commands and what they do.");
        System.out.println();
        System.out.println("To start select the mode: 'Tui' or 'Gui', or 'Exit' to exit.");
    }
}
