package it.polimi.ingsw.view.tui.states.gameScreens.goodActionState;

import it.polimi.ingsw.view.tui.states.GameTuiScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class GoodManagerTuiScreen extends GameTuiScreen {
    protected ArrayList<Integer> from;
    protected ArrayList<Integer> to;
    

    public GoodManagerTuiScreen(List<String> options) {
        super(options);
    }


}
