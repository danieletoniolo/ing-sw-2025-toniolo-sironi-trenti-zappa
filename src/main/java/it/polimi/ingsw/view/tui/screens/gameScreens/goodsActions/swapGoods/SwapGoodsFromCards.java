package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;
import java.util.Arrays;

public class SwapGoodsFromCards extends ManagerSwapGoodCards {
    private final TuiScreenView oldScreen;

    public SwapGoodsFromCards(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            for (GoodView good : fromStorage.getGoods()) {
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
        for (GoodView good : fromStorage.getGoods()) {
            if (good != null) {
                num++;
            }
        }
        if (selected == num + 1) {
            destroyStatics();
            setMessage(null);
            return oldScreen;
        }

        StringBuilder line = new StringBuilder();
        for (Integer value : fromList) {
            line.append(value != null ? GoodView.fromValue(value).drawTui() : "| |").append(" ");
        }

        if (selected == num) {
            setMessage("You are swapping " + line + "from (" + fromStorage.getRow() + " " + fromStorage.getCol() + ")");
            return new SwapGoodsWithCards(Arrays.asList(withStorage.getGoods()), oldScreen);
        }

        GoodView goodV = null;
        int i = 0;
        for (GoodView good : fromStorage.getGoods()) {
            if (good != null) {
                if (i == selected) {
                    goodV = good;
                    break;
                }
                i++;
            }
        }
        fromList.add(goodV != null ? goodV.getValue() : null);
        fromStorage.removeGood(goodV);

        line = new StringBuilder();
        for (Integer value : fromList) {
            line.append(value != null ? GoodView.fromValue(value).drawTui() : "| |").append(" ");
        }

        setMessage("You are swapping " + line + "from (" + fromStorage.getRow() + " " + fromStorage.getCol() + ")");
        return new SwapGoodsFromCards(oldScreen);
    }


    @Override
    protected String lineBeforeInput() {
        return "Select goods to swap FROM (" + fromStorage.getRow() + " " + fromStorage.getCol() + "):";
    }
}
