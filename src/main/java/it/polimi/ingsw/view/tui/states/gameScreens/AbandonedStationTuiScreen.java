package it.polimi.ingsw.view.tui.states.gameScreens;

import it.polimi.ingsw.view.tui.states.GameTuiScreen;
import it.polimi.ingsw.view.tui.states.TuiScreenView;

import java.util.List;

public class AbandonedStationTuiScreen extends GameTuiScreen {

    public AbandonedStationTuiScreen() {
        super(List.of("Accept", "Refuse"));
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

    @Override
    public String lineBeforeInput() {
        return "You have reached an abandoned station. Do you want to accept the offer?";
    }
}
