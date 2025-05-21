package view.tui.states.gameScreens.goodActionState;

import org.javatuples.Pair;
import view.miniModel.MiniModel;
import view.miniModel.components.StorageView;
import view.miniModel.good.GoodView;
import view.tui.states.ScreenTuiView;

import java.util.ArrayList;
import java.util.List;

public class SwapGoodsFromScreenTui extends GoodManagerScreenTui {

    public SwapGoodsFromScreenTui() {
        super(getOptions());
    }

    private static List<String> getOptions() {
        List<String> newOptions = new ArrayList<>();

        MiniModel.getInstance().clientPlayer.getShip().getMapStorages().forEach(
                (key, value) -> {
                    if (((StorageView) value).getGoods() != null) {
                        for (int i = 0; i < ((StorageView) value).getGoods().length; i++) {
                            if (((StorageView) value).getGoods()[i] != null) {
                                newOptions.add("Swap " + ((StorageView) value).getGoods()[i].drawTui() + " from (" + value.getRow() + "," + value.getCol() + ")");
                            }
                        }
                    }
                });
        return newOptions;
    }

    @Override
    public ScreenTuiView isViewCommand(){
        return null;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a good to swap:";
    }
}
