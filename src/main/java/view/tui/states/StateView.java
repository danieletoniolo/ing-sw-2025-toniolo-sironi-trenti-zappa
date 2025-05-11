package view.tui.states;

import view.tui.input.Command;

public abstract class StateView {
    public static String nickName;

    public abstract StateView isValidCommand(Command command);

    public abstract void printTui();


}
