package it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons;

import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.engineActions.ChooseDoubleEngineCards;

import java.util.List;

public class OpenSpaceCards extends CardsGame {

    public OpenSpaceCards() {
        super(List.of("Turn on engines"));
        setMessage("Now's the time! Turn on your engines and go as fast as you can!");
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            spaceShipView = clientPlayer.getShip().clone();
            return new ChooseDoubleEngineCards();
        }

        return this;
    }
}
