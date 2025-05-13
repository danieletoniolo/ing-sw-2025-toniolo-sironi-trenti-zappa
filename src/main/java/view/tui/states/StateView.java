package view.tui.states;

import view.tui.input.Command;

public interface StateView {
    StateView isValidCommand(Command command);

    void printTui();


}
