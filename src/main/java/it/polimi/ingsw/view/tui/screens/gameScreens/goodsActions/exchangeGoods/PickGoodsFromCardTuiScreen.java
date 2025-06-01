package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public class PickGoodsFromCardTuiScreen extends ManagerExchangeGoodsTuiScreen{
    private TuiScreenView oldScreen;
    private List<GoodView> goods;

    public PickGoodsFromCardTuiScreen(List<GoodView> goods, TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            for (GoodView good : goods) {
                add(good.drawTui());
            }
            add("Done");
            add("Cancel");
        }});

        this.goods = goods;
        this.oldScreen = oldScreen;
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == options.size() - MiniModel.getInstance().getOtherPlayers().size() - 2) {
            exchanges.add(new Triplet<>(
                    new ArrayList<>(goodsToGet),
                    new ArrayList<>(goodsToLeave),
                    storage.getID()));
            goodsToLeave.clear();
            goodsToGet.clear();
            return new StorageExchangeTuiScreen(oldScreen);
        }

        if (selected == options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1) {
            destroyStatics();
            return oldScreen;
        }

        goodsToGet.add(goods.get(selected).getValue());
        storage.addGood(goods.get(selected));
        goods.remove(selected);

        StringBuilder line = new StringBuilder();
        for (Integer good : goodsToGet) {
            line.append(GoodView.fromValue(good).drawTui()).append(" ");
        }

        TuiScreenView newScreen = new PickGoodsFromCardTuiScreen(goods, oldScreen);
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
