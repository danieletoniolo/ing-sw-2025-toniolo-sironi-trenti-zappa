package view.tui.states.gameScreens.goodActionState;

import java.util.List;

public class GoodDecisionScreenTui extends GoodManagerScreenTui{

    public GoodDecisionScreenTui() {
        super(List.of("Swap goods", "Take good from the card", "Drop good from the ship", "Done"));
    }
}
