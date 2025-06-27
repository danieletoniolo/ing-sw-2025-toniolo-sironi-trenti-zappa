package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * The DropGoodsCards class manages the TUI screen for dropping goods cards during an exchange action.
 * It extends ManagerExchangeGoodsCards and provides the logic for selecting and removing goods from storage,
 * as well as handling user navigation (done/cancel) in the drop flow.
 */
public class DropGoodsCards extends ManagerExchangeGoodsCards {
    private final TuiScreenView oldScreen;

    /**
     * Constructs a DropGoodsCards screen.
     * Initializes the selectable options with the current goods in storage, plus "Done" and "Cancel".
     *
     * @param oldScreen the previous TUI screen to return to if the action is cancelled
     */
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

    /**
     * Handles the logic for updating the screen based on the user's selection.
     * If "Done" is selected, proceeds to the next step in the exchange.
     * If "Cancel" is selected, returns to the previous screen.
     * Otherwise, removes the selected good from storage and updates the message.
     *
     * @return the next TuiScreenView to display
     */
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

    /**
     * Returns the prompt line to display before user input.
     *
     * @return the prompt string
     */
    @Override
    protected String lineBeforeInput() {
        return "Select goods to DROP:";
    }
}
