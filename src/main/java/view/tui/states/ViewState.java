package view.tui.states;

import view.tui.input.Command;

public abstract class ViewState {
    public abstract ViewState validCommand(Command command);

    public abstract void printTui();
}
