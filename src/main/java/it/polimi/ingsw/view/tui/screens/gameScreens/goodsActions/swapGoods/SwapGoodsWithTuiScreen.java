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
    private List<GoodView> oldGoods;

    public SwapGoodsWithTuiScreen(List<GoodView> goods, TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            if (times > 0) {
                for (GoodView good : goods) {
                    String line;
                    if (good != null) {
                        line = " " + good.drawTui() + " ";
                    } else {
                        line = "| |";
                    }
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

        int num = times == 0 ? 0 : oldGoods.size();
        if (selected == num) {
            StatusEvent status = SwapGoods.requester(Client.transceiver, new Object()).request(
                    new SwapGoods(MiniModel.getInstance().getUserID(), fromStorage.getID(), withStorage.getID(), fromList, withList));
            if (status.get().equals("POTA")) {
                oldScreen.setMessage(((Pota) status).errorMessage());
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

        times--;
        GoodView goodV = null;
        int i = 0;
        for (GoodView good : oldGoods) {
            if (i == selected) {
                goodV = good;
                break;
            }
            i++;
        }
        withList.add(goodV != null ? goodV.getValue() : null);

        List<GoodView> newGoods = new ArrayList<>();
        for (GoodView good : oldGoods) {
            if (!java.util.Objects.equals(goodV, good)) {
                newGoods.add(good);
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

        TuiScreenView newScreen = new SwapGoodsWithTuiScreen(newGoods, oldScreen);
        newScreen.setMessage("You are swapping " + line + "from (" + fromStorage.getRow() + " " + fromStorage.getCol() + ") with "
                + line2 + "in (" + withStorage.getRow() + " " + withStorage.getCol() + ")");
        return newScreen;
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
