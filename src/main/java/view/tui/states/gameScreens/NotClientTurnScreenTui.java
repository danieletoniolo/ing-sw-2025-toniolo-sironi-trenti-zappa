package view.tui.states.gameScreens;

import view.tui.states.GameScreenTui;

public class NotClientTurnScreenTui extends GameScreenTui {

    public NotClientTurnScreenTui() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "View other players' ship while waiting for your turn.";
    }
}
