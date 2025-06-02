package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoodsActions;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwapGoodsFromTuiScreen extends ManagerSwapGoodTuiScreen{
    private final TuiScreenView oldScreen;

    public SwapGoodsFromTuiScreen(TuiScreenView oldScreen) {
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
            TuiScreenView newScreen = new SwapGoodsWithTuiScreen(Arrays.asList(withStorage.getGoods()), oldScreen);
            newScreen.setMessage("You are swapping " + line + "from (" + fromStorage.getRow() + " " + fromStorage.getCol() + ")");
            return newScreen;
        }

        times++;
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

        TuiScreenView newScreen = new SwapGoodsFromTuiScreen(oldScreen);
        newScreen.setMessage("You are swapping " + line + "from (" + fromStorage.getRow() + " " + fromStorage.getCol() + ")");
        return newScreen;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.SwapFrom;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select goods to swap FROM (" + fromStorage.getRow() + " " + fromStorage.getCol() + "):";
    }
}
