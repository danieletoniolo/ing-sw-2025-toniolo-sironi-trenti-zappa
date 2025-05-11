package view.tui.states;

import view.tui.Menu;
import view.tui.input.Command;

public class MenuStateView extends StateView {

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
