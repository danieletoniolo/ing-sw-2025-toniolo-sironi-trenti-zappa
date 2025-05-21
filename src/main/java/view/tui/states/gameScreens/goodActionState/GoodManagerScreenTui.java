package view.tui.states.gameScreens.goodActionState;

import view.tui.states.GameScreenTui;

import java.util.ArrayList;
import java.util.List;

public abstract class GoodManagerScreenTui extends GameScreenTui {
    protected ArrayList<Integer> from;
    protected ArrayList<Integer> to;

    public GoodManagerScreenTui(List<String> options) {
        super(options);
    }


}
