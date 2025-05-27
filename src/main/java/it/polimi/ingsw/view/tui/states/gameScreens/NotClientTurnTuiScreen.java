package it.polimi.ingsw.view.tui.states.gameScreens;

import it.polimi.ingsw.view.tui.states.GameTuiScreen;

public class NotClientTurnTuiScreen extends GameTuiScreen {

    public NotClientTurnTuiScreen() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "View other players' ship while waiting for your turn.";
    }
}
