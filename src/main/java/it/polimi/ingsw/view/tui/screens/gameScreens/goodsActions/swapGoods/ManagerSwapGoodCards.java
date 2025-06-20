package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.ArrayList;
import java.util.List;

public abstract class ManagerSwapGoodCards extends CardsGame {
    protected static StorageView fromStorage;
    protected static ArrayList<Integer> fromList;
    protected static StorageView withStorage;
    protected static ArrayList<Integer> withList;

    public ManagerSwapGoodCards(List<String> options) {
        super(options);

        if (fromList == null) {
            fromList = new ArrayList<>();
        }
        if (withList == null) {
            withList = new ArrayList<>();
        }
    }

    public static void destroyStatics() {
        fromList = null;
        fromStorage = null;
        withList = null;
        withStorage = null;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }
}
