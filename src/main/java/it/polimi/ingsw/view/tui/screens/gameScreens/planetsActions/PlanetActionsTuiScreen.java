package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.PlanetsTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.swapGoodsState.SwapGoodsToTuiScreen;

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
                return new SwapGoodsToTuiScreen();
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
