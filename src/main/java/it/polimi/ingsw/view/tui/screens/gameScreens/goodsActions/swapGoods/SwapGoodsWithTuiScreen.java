package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;
import java.util.List;

public class SwapGoodsWithTuiScreen extends ManagerSwapGoodTuiScreen {
    private final TuiScreenView oldScreen;
    private final List<GoodView> oldGoods;

    public SwapGoodsWithTuiScreen(List<GoodView> goods, TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            for (GoodView good : goods) {
                String line;
                if (good != null) {
                    line = " " + good.drawTui() + " ";
                    add("Swap" + line + "from (" + withStorage.getRow() + "," + withStorage.getCol() + ")");
                }
            }
            add("Done");
            add("Cancel");
        }});
        this.oldScreen = oldScreen;
        this.oldGoods = goods;
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = 0;
        for (GoodView good : oldGoods) {
            if (good != null) {
                num++;
            }
        }

        if (selected == num) {
            StatusEvent status = SwapGoods.requester(Client.transceiver, new Object()).request(
                    new SwapGoods(MiniModel.getInstance().getUserID(), fromStorage.getID(), withStorage.getID(), fromList, withList));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
            }
            else{
                setMessage(null);
            }
            destroyStatics();
            return oldScreen;
        }

        if (selected == num + 1) {
            destroyStatics();
            setMessage(null);
            return oldScreen;
        }

        GoodView goodV = null;
        int i = 0;
        for (GoodView good : oldGoods) {
            if (good != null) {
                if (i == selected) {
                    goodV = good;
                    break;
                }
            }
            i++;
        }
        if (goodV != null) {
            withList.add(goodV.getValue());
        }

        List<GoodView> newGoods = new ArrayList<>();
        for (i = 0; i < oldGoods.size(); i++) {
            if (i != selected) {
                newGoods.add(oldGoods.get(i));
            }
        }
        withStorage.removeGood(goodV);

        StringBuilder line = new StringBuilder();
        for (Integer value : fromList) {
            line.append(GoodView.fromValue(value).drawTui()).append(" ");
        }

        StringBuilder line2 = new StringBuilder();
        for (Integer value : withList) {
            line2.append(value != null ? GoodView.fromValue(value).drawTui() : "| |").append(" ");
        }

        setMessage("You are swapping " + line + "from (" + fromStorage.getRow() + " " + fromStorage.getCol() + ") with "
                + line2 + "in (" + withStorage.getRow() + " " + withStorage.getCol() + ")");
        return new SwapGoodsWithTuiScreen(newGoods, oldScreen);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.SwapTo;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select goods to swap WITH (" + withStorage.getRow() + " " + withStorage.getCol() + "):";
    }
}
