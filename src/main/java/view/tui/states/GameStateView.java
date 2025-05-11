package view.tui.states;

import view.tui.input.Command;

public class GameStateView extends StateView{

    @Override
    public StateView isValidCommand(Command command) {
        return null; // Placeholder for the next state
    }

    @Override
    public void printTui() {
        System.out.println("Game state view");
    }
}
