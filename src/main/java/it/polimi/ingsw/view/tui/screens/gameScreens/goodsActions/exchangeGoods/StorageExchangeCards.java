package it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.exchangeGoods;

import it.polimi.ingsw.event.game.clientToServer.goods.ExchangeGoods;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;

import java.util.ArrayList;

public class StorageExchangeCards extends ManagerExchangeGoodsCards {
    private final TuiScreenView oldScreen;

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

    @Override
    protected String lineBeforeInput() {
        return "Select a STORAGE:";
    }
}
