package it.polimi.ingsw.view.tui.states.gameScreens.planetsActions;

import it.polimi.ingsw.view.tui.states.TuiScreenView;
import it.polimi.ingsw.view.tui.states.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.states.gameScreens.PlanetsTuiScreen;

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
