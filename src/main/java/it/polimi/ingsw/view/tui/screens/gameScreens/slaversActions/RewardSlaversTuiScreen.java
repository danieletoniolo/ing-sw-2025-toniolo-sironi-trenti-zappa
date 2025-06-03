package it.polimi.ingsw.view.tui.screens.gameScreens.slaversActions;

import it.polimi.ingsw.view.tui.TuiManager;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.List;

public class RewardSlaversTuiScreen extends GameTuiScreen {

    public RewardSlaversTuiScreen() {
        super(List.of("Claim rewards", "Refuse rewards"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        switch (selected) {
            case 0:

                break;
            case 1:

                break;
        }

        return this;
    }
}
