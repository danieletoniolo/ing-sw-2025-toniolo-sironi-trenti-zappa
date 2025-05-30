package it.polimi.ingsw.view.tui.states.gameScreens.goodActionState;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.StorageView;

import java.util.ArrayList;
import java.util.List;

public class SwapGoodsFromTuiScreen extends GoodManagerTuiScreen {

    public SwapGoodsFromTuiScreen() {
        super(getOptions());
    }

    private static List<String> getOptions() {
        List<String> options = new ArrayList<>();

        MiniModel.getInstance().getClientPlayer().getShip().getMapStorages().forEach(
                (key, value) -> {
                    if (((StorageView) value).getGoods() != null) {
                        for (int i = 0; i < ((StorageView) value).getGoods().length; i++) {
                            if (((StorageView) value).getGoods()[i] != null) {
                                options.add("Swap " + ((StorageView) value).getGoods()[i].drawTui() + " from (" + value.getRow() + "," + value.getCol() + ")");
                            }
                        }
                    }
                });
        return options;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a good to swap:";
    }
}
