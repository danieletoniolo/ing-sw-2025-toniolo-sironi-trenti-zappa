package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.view.tui.screens.CardsGame;

public class NotClientTurnCards extends CardsGame {

    public NotClientTurnCards() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "View other players' ship while waiting for your turn.";
    }
}
