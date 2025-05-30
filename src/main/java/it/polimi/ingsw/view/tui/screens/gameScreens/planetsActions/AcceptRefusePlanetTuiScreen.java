package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.PlanetsTuiScreen;

import java.util.List;

public class AcceptRefusePlanetTuiScreen extends PlanetsTuiScreen {

    public AcceptRefusePlanetTuiScreen() {
        super(List.of("Accept", "Refuse"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        switch (selected) {
            case 0:
                return new PlanetActionsTuiScreen();
            case 1:
                return new NotClientTurnTuiScreen();
        }

        return this;
    }
}
