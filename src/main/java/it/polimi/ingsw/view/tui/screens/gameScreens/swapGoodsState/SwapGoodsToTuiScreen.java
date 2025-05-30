package it.polimi.ingsw.view.tui.screens.gameScreens.swapGoodsState;

public class SwapGoodsToTuiScreen extends GoodManagerTuiScreen {

    public SwapGoodsToTuiScreen() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a good to swap to:";
    }
}
