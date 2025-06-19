package it.polimi.ingsw.view.tui.screens.crewScreens;

import it.polimi.ingsw.view.tui.screens.ModifyCrew;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

public class WaitingCrew extends ModifyCrew {

    public WaitingCrew() {
        super(null);
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        return this;
    }
}
