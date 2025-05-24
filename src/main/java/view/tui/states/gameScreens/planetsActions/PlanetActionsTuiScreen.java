package view.tui.states.gameScreens.planetsActions;

import view.tui.states.TuiScreenView;
import view.tui.states.gameScreens.NotClientTurnTuiScreen;
import view.tui.states.gameScreens.PlanetsTuiScreen;

import java.util.List;

public class PlanetActionsTuiScreen extends PlanetsTuiScreen {

    public PlanetActionsTuiScreen() {
        super(List.of("Swap goods", "Select a planet", "Drop good from the ship", "Done"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        switch (selected) {
            case 0:
                //return new SwapGoodsToTuiScreen();
            case 1:
                return new SelectPlanetTuiScreen();
            case 2:
                //return new DropGoodFromShipTuiScreen();
            case 3:
                return new NotClientTurnTuiScreen();
        }

        return this;
    }
}
