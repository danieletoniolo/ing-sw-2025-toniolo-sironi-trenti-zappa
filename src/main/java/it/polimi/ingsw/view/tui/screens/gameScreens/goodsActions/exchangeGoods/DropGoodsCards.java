package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class DropGoodsCards extends ManagerExchangeGoodsCards {
    private final TuiScreenView oldScreen;

    public DropGoodsCards(TuiScreenView oldScreen) {
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

        int num = 0;
        for(GoodView good : storage.getGoods()) {
            if (good != null) {
                num++;
            }
        }
        if (selected == num) {
            return new PickGoodsFromCardCards(remainCopy, oldScreen);
        }

        if (selected == num + 1) {
            destroyStatics();
            setMessage(null);
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

        setMessage("You are dropping " + line);
        return new DropGoodsCards(oldScreen);
    }

    @Override
    protected String lineBeforeInput() {
        return "Select goods to DROP:";
    }
}
