package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.event.game.clientToServer.goods.ExchangeGoods;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;

import java.util.ArrayList;

/**
 * The StorageExchangeCards class manages the selection and exchange of goods
 * from the player's storage in the TUI (Text User Interface) of the game.
 * It extends ManagerExchangeGoodsCards and provides options to select storages,
 * finish the exchange, or cancel the operation.
 */
public class StorageExchangeCards extends ManagerExchangeGoodsCards {
    /**
     * Reference to the previous screen to return to after the exchange.
     */
    private final TuiScreenView oldScreen;

    /**
     * Constructs a StorageExchangeCards screen with selectable storage options.
     *
     * @param oldScreen the previous TuiScreenView to return to
     */
    public StorageExchangeCards(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            spaceShipView.getMapStorages().forEach(
                    (key, value) -> {
                        if (exchanges == null || exchanges.stream().noneMatch(triplet -> triplet.getValue2().equals(key))) {
                            add("Select the storage " + "(" + value.getRow() + "," + value.getCol() + ")");
                        }
                    });
            add("Finish exchange");
            add("Cancel");
        }});
        this.oldScreen = oldScreen;
    }

    /**
     * Handles the logic for setting the new screen based on the user's selection.
     * Sends the exchange request, returns to the previous screen, or proceeds to
     * the next step in the exchange process.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = 0;
        for (var entry : spaceShipView.getMapStorages().entrySet()) {
            if (exchanges == null || exchanges.stream().noneMatch(triplet -> triplet.getValue2().equals(entry.getKey()))) {
                num++;
            }
        }
        if (selected == num) {
            // Send the request to exchange goods
            StatusEvent status = ExchangeGoods.requester(Client.transceiver, new Object()).request(
                    new ExchangeGoods(MiniModel.getInstance().getUserID(), exchanges));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
            }
            else {
                MenuGoodsCards.setCardGoods(remainCopy);
                setMessage(null);
            }
            destroyStatics();
            return oldScreen;

        }

        if (selected == num + 1) {
            setMessage(null);
            destroyStatics();
            return oldScreen;
        }

        int cont = 0;
        int ID = -1;
        for (var entry : spaceShipView.getMapStorages().entrySet()) {
            if (exchanges == null || exchanges.stream().noneMatch(triplet -> triplet.getValue2().equals(entry.getKey()))) {
                if (cont == selected) {
                    ID = entry.getKey();
                    break;
                }
                cont++;
            }
        }
        storage = spaceShipView.getMapStorages().get(ID);
        setMessage("You selected the storage at (" + storage.getRow() + "," + storage.getCol() + ")");
        return new DropGoodsCards(oldScreen);
    }

    /**
     * Returns the prompt line to display before the user input.
     *
     * @return the prompt string
     */
    @Override
    protected String lineBeforeInput() {
        return "Select a STORAGE:";
    }
}
