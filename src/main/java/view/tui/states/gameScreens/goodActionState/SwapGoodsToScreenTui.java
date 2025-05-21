package view.tui.states.gameScreens.goodActionState;

public class SwapGoodsToScreenTui extends GoodManagerScreenTui {

    public SwapGoodsToScreenTui() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a good to swap to:";
    }
}
