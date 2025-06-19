package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.*;

public class LooseGoodsCards extends CardsGame {
    private static List<Integer> storageIDs;
    private static List<Integer> looseGoods;
    private static boolean reset;
    private final TuiScreenView nextScreen;

    public LooseGoodsCards(TuiScreenView nextScreen) {
        super(new ArrayList<>(){{
            if (!reset) {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            }
            if (!reset) reset = true;
            spaceShipView.getMapStorages().values().stream()
                    .filter(storage -> java.util.Arrays.stream(storage.getGoods()).anyMatch(Objects::nonNull))
                    .forEach(storage -> add("(" + storage.getRow() + " " + storage.getCol() + ")"));
            add("Cancel");
            add("Done");
        }});
        if (storageIDs == null) {
            storageIDs = new ArrayList<>();
        }
        if (looseGoods == null) {
            looseGoods = new ArrayList<>();
        }
        this.nextScreen = nextScreen;
    }

    @Override
    protected String lineBeforeInput() {
        return "Choose a storage unit to discard the most expensive goods.";
    }

    private void destroyStatic() {
        storageIDs = null;
        looseGoods = null;
        reset = false;
        setMessage(null);
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapStorages().values().stream()
                .filter(storage -> java.util.Arrays.stream(storage.getGoods()).anyMatch(Objects::nonNull))
                .count();

        if (selected == num) {
            destroyStatic();
            return new LooseGoodsCards(nextScreen);
        }

        if (selected == num + 1) {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 0, storageIDs));
            destroyStatic();
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return new LooseGoodsCards(nextScreen);
            }
            spaceShipView = clientPlayer.getShip();
            return nextScreen;
        }

        spaceShipView.getMapStorages().entrySet().stream()
                .filter(entry -> java.util.Arrays.stream(entry.getValue().getGoods()).anyMatch(Objects::nonNull))
                .skip(selected)
                .findFirst()
                .ifPresent(entry -> {
                    storageIDs.add(entry.getKey());
                    looseGoods.add(entry.getValue().removeOneGood().getValue());
                });

        StringBuilder line = new StringBuilder();
        for (Integer integer : looseGoods) {
            line.append(GoodView.fromValue(integer).drawTui()).append(" ");
        }

        setMessage("You are dropping " + line);
        return new LooseGoodsCards(nextScreen);
    }
}
