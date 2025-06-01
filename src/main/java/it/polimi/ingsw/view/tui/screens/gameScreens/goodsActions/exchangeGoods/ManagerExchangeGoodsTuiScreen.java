package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public abstract class ManagerExchangeGoodsTuiScreen extends GameTuiScreen {
    protected static List<Triplet<List<Integer>, List<Integer>, Integer>> exchanges;
    protected static List<Integer> goodsToGet;
    protected static List<Integer> goodsToLeave;
    protected static StorageView storage;

    public ManagerExchangeGoodsTuiScreen(List<String> options) {
        super(options);

        if (exchanges == null) {
            exchanges = new ArrayList<>();
        }
        if (goodsToGet == null) {
            goodsToGet = new ArrayList<>();
        }
        if (goodsToLeave == null) {
            goodsToLeave = new ArrayList<>();
        }
    }

    protected void destroyStatics() {
        exchanges = null;
        goodsToGet = null;
        goodsToLeave = null;
        storage = null;
        spaceShipView = clientPlayer.getShip();
    }
}
