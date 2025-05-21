package view.tui.states.gameScreens.goodActionState;

import java.util.ArrayList;

public class SelectGoodFromTheCardScreen extends GoodManagerScreenTui {

    public SelectGoodFromTheCardScreen() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a good from the card:";
    }
}
