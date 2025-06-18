package it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons;

import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.engineActions.ChooseDoubleEngineTuiScreen;

import java.util.List;

public class OpenSpaceTuiScreen extends GameTuiScreen {

    public OpenSpaceTuiScreen() {
        super(List.of("Turn on engines"));
        setMessage("Now's the time! Turn on your engines and go as fast as you can!");
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            spaceShipView = clientPlayer.getShip().clone();
            return new ChooseDoubleEngineTuiScreen();
        }

        return this;
    }
}
