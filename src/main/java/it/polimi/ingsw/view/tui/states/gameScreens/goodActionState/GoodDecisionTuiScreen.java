package it.polimi.ingsw.view.tui.states.gameScreens.goodActionState;

import java.util.List;

public class GoodDecisionTuiScreen extends GoodManagerTuiScreen {

    public GoodDecisionTuiScreen() {
        super(List.of("Swap goods", "Take good from the card", "Drop good from the ship", "Done"));
    }
}
