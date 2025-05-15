package view.tui.states;

import view.tui.input.Command;

import java.util.ArrayList;
import java.util.List;

public class BuidingStateView implements StateView{
    private ArrayList<String> validCommands = (ArrayList<String>) List.of("view");

    @Override
    public StateView isValidCommand(Command command) {
        return null; // Placeholder for the next state
    }

    @Override
    public void printTui() {
        System.out.println("Building state view");
    }
}
