package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.cards.AbandonedStationView;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.miniModel.cards.SmugglersView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public abstract class ManagerExchangeGoodsTuiScreen extends GameTuiScreen {
    protected static List<Triplet<List<Integer>, List<Integer>, Integer>> exchanges;
    protected static List<Integer> goodsToGet;
    protected static List<Integer> goodsToLeave;
    protected static StorageView storage;
    protected static List<GoodView> copy;

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
        if (copy == null) {
            copy = new ArrayList<>();
            CardView card = shuffledDeckView.getDeck().peek();
            switch (card.getCardViewType()) {
                case PLANETS:
                    for (GoodView good : ((PlanetsView) card).getPlanet(((PlanetsView) card).getPlanetSelected())) {
                        copy.add(GoodView.fromValue(good.getValue()));
                    }
                    break;
                case SMUGGLERS:
                    for (GoodView good : ((SmugglersView) card).getGoods()) {
                        copy.add(GoodView.fromValue(good.getValue()));
                    }
                    break;
                case ABANDONEDSTATION:
                    for (GoodView good : ((AbandonedStationView) card).getGoods()) {
                        copy.add(GoodView.fromValue(good.getValue()));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void destroyStatics() {
        exchanges = null;
        goodsToGet = null;
        goodsToLeave = null;
        storage = null;
        copy = null;
        spaceShipView = clientPlayer.getShip();
    }
}
