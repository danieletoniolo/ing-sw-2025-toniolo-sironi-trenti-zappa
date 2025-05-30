package it.polimi.ingsw.view.tui.screens.gameScreens.swapGoodsState;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;

public class SwapGoodsFromTuiScreen extends GoodManagerTuiScreen {
    private TuiScreenView oldScreen;

    public SwapGoodsFromTuiScreen(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            spaceShipView.getMapStorages().forEach(
                    (key, value) -> {
                        if (value.getGoods() != null) {
                            for (int i = 0; i < value.getGoods().length; i++) {
                                if (value.getGoods()[i] != null) {
                                    add("Swap " + value.getGoods()[i].drawTui() + " from (" + value.getRow() + "," + value.getCol() + ")");
                                }
                            }
                        }
                    });
        }});
        this.oldScreen = oldScreen;
    }

    @Override
    public TuiScreenView setNewScreen() {
        final int[] cont = {0};
        final int[] ID = {-1, -1};
        final boolean[] found = {false};
        spaceShipView.getMapStorages().forEach(
                (key, value) -> {
                    if (value.getGoods() != null && !found[0]) {
                        for (int i = 0; i < value.getGoods().length; i++) {
                            if (value.getGoods()[i] != null && cont[0] == selected) {
                                ID[0] = key;
                                ID[1] = value.getGoods()[i].getValue();
                                found[0] = true;
                            }else{
                                cont[0]++;
                            }
                        }
                    }
                });
        StorageView storage = spaceShipView.getMapStorages().get(ID[0]);
        storage.removeGood(GoodView.fromValue(ID[1]));
        //message += GoodView.fromValue(ID[1]) + "(" + value.getRow() + "," + value.getCol() + ")";
        return null;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.SwapGoodsFrom;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a good to swap:";
    }
}
