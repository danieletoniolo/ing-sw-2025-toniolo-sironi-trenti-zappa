package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.swapGoods;

import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for managing the swap of goods with cards in the TUI.
 * Allows the user to select goods to swap from a list and handles the swap logic.
 */
public class SwapGoodsWithCards extends ManagerSwapGoodCards {
    private final TuiScreenView oldScreen;
    private final List<GoodView> oldGoods;

    /**
     * Constructs a new SwapGoodsWithCards screen.
     *
     * @param goods      the list of goods available for swapping
     * @param oldScreen  the previous screen to return to after the swap
     */
    public SwapGoodsWithCards(List<GoodView> goods, TuiScreenView oldScreen) {
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

    /**
     * Handles the logic for setting the new screen after a swap action.
     * Manages user selection, swap request, and screen transitions.
     *
     * @return the next TuiScreenView to display
     */
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
            // Send request to swap goods
            StatusEvent status = SwapGoods.requester(Client.transceiver, new Object()).request(
                    new SwapGoods(MiniModel.getInstance().getUserID(), fromStorage.getID(), withStorage.getID(), fromList, withList));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
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
        return new SwapGoodsWithCards(newGoods, oldScreen);
    }

    /**
     * Returns the line to display before the user input prompt.
     *
     * @return the prompt line as a String
     */
    @Override
    protected String lineBeforeInput() {
        return "Select goods to swap WITH (" + withStorage.getRow() + " " + withStorage.getCol() + "):";
    }
}
