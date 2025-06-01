package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;

import java.util.List;

public class PlanetsTuiScreen extends GameTuiScreen {

    public PlanetsTuiScreen() {
        super(List.of("Accept", "Refuse"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        switch (selected) {
            case 0:
                return new SelectPlanetTuiScreen(this);
            case 1:
                return new NotClientTurnTuiScreen();
        }

        return this;
    }
}
