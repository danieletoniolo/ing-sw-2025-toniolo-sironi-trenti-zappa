package it.polimi.ingsw.view.tui.screens.gameScreens.openSpaceAcitons;

import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.engineActions.ChooseDoubleEngineTuiScreen;

import java.util.List;

public class OpenSpaceTuiScreen extends GameTuiScreen {

    public OpenSpaceTuiScreen() {
        super(List.of("Active engines"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        spaceShipView = clientPlayer.getShip().clone();
        return new ChooseDoubleEngineTuiScreen(this);
    }
}
