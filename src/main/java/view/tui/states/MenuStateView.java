package view.tui.states;

import view.tui.input.Command;

public class MenuStateView implements StateView {

    public MenuStateView() {

    }

    @Override
    public StateView isValidCommand(Command command) {
        return null; // Placeholder for the next state
    }

    @Override
    public void printTui() {
        System.out.println("Menu state view");
    }
}
