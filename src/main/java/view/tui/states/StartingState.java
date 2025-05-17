package view.tui.states;

import org.jline.terminal.Terminal;
import view.tui.TerminalUtils;
import view.tui.input.Command;

import java.util.ArrayList;

public class StartingState {
    ArrayList<String> validCommands;

    public StartingState() {
        validCommands = new ArrayList<>();
        validCommands.add("tui");
        validCommands.add("gui");
        validCommands.add("exit");
        validCommands.add("help");
    }

    public StateView readInput(Terminal terminal) {

        return null;
    }

    public void printTui(Terminal terminal) {
        System.out.println("Lost? Type 'HELP' to get a rundown of all commands and what they do.");
        System.out.println();
        System.out.println("To start select the mode: 'Tui' or 'Gui', or 'Exit' to exit.");
    }
}
