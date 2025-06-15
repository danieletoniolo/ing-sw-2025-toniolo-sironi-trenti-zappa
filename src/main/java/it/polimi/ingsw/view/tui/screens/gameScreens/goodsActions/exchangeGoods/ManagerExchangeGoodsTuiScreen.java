package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsTuiScreen;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public abstract class ManagerExchangeGoodsTuiScreen extends GameTuiScreen {
    protected static List<Triplet<List<Integer>, List<Integer>, Integer>> exchanges;
    protected static List<Integer> goodsToGet;
    protected static List<Integer> goodsToLeave;
    protected static StorageView storage;
    protected static List<GoodView> remainCopy;

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
        if (remainCopy == null) {
            remainCopy = new ArrayList<>(MenuGoodsTuiScreen.getCopy());
        }
    }

    public void destroyStatics() {
        exchanges = null;
        goodsToGet = null;
        goodsToLeave = null;
        storage = null;
        remainCopy = null;
        spaceShipView = clientPlayer.getShip();
    }
}
