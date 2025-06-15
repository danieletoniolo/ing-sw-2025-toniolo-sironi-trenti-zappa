package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public class PickGoodsFromCardTuiScreen extends ManagerExchangeGoodsTuiScreen{
    private final TuiScreenView oldScreen;
    private final List<GoodView> goodsOnCard;

    public PickGoodsFromCardTuiScreen(List<GoodView> goodsOnCard, TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            for (GoodView good : goodsOnCard) {
                add(good.drawTui());
            }
            add("Done");
            add("Cancel");
        }});

        this.goodsOnCard = goodsOnCard;
        this.oldScreen = oldScreen;
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == goodsOnCard.size()) {
            exchanges.add(new Triplet<>(
                    new ArrayList<>(goodsToGet),
                    new ArrayList<>(goodsToLeave),
                    storage.getID()));
            goodsToLeave.clear();
            goodsToGet.clear();
            return new StorageExchangeTuiScreen(oldScreen);
        }

        if (selected == goodsOnCard.size() + 1) {
            destroyStatics();
            return oldScreen;
        }

        goodsToGet.add(goodsOnCard.get(selected).getValue());
        storage.addGood(goodsOnCard.get(selected));
        goodsOnCard.remove(selected);

        StringBuilder line = new StringBuilder();
        for (Integer good : goodsToGet) {
            line.append(GoodView.fromValue(good).drawTui()).append(" ");
        }

        TuiScreenView newScreen = new PickGoodsFromCardTuiScreen(goodsOnCard, oldScreen);
        newScreen.setMessage("You have selected " + line);
        return newScreen;
    }

    @Override
    protected String lineBeforeInput() {
        return switch (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getCardViewType()) {
            case PLANETS -> "Choose GOODS from the planet:";
            case SMUGGLERS -> "Claim your REWARDS:";
            case ABANDONEDSTATION -> "Choose your GOODS:";
            default -> "Commands:";
        };
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.PickFromCard;
    }
}
