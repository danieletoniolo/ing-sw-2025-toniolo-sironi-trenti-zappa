package it.polimi.ingsw.view.tui.states.gameScreens.goodActionState;

public class SwapGoodsToTuiScreen extends GoodManagerTuiScreen {

    public SwapGoodsToTuiScreen() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a good to swap to:";
    }
}
