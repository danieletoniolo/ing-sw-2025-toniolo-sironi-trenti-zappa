package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.cards.AbandonedStationView;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.miniModel.cards.SmugglersView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;
import java.util.List;

public class DropGoodsTuiScreen extends ManagerExchangeGoodsTuiScreen{
    private final TuiScreenView oldScreen;

    public DropGoodsTuiScreen(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            for(GoodView good : storage.getGoods()) {
                if (good != null) {
                    add(good.drawTui());
                }
            }
            add("Done");
            add("Cancel");
        }});
        this.oldScreen = oldScreen;
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == options.size() - MiniModel.getInstance().getOtherPlayers().size() - 2) {
            List<GoodView> copy = new ArrayList<>();
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

            return new PickGoodsFromCardTuiScreen(copy, oldScreen);
        }

        if (selected == options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1) {
            destroyStatics();
            return oldScreen;
        }

        GoodView goodV = null;
        int i = 0;
        for (GoodView good : storage.getGoods()) {
            if (good != null) {
                if (i == selected) {
                    goodV = good;
                    break;
                }
                i++;
            }
        }

        storage.removeGood(goodV);
        goodsToLeave.add(goodV != null ? goodV.getValue() : 0);

        StringBuilder line = new StringBuilder();
        for (Integer good : goodsToLeave) {
            line.append(GoodView.fromValue(good).drawTui()).append(" ");
        }

        TuiScreenView newScreen = new DropGoodsTuiScreen(oldScreen);
        newScreen.setMessage("You are dropping " + line);
        return newScreen;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select goods to DROP:";
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.DropGoods;
    }
}
