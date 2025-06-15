package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class ManagerSwapGoodTuiScreen extends GameTuiScreen {
    protected static StorageView fromStorage;
    protected static ArrayList<Integer> fromList;
    protected static StorageView withStorage;
    protected static ArrayList<Integer> withList;

    public ManagerSwapGoodTuiScreen(List<String> options) {
        super(options);

        if (fromList == null) {
            fromList = new ArrayList<>();
        }
        if (withList == null) {
            withList = new ArrayList<>();
        }
    }

    protected void destroyStatics() {
        fromList = null;
        fromStorage = null;
        withList = null;
        withStorage = null;
        spaceShipView = clientPlayer.getShip();
    }
}
