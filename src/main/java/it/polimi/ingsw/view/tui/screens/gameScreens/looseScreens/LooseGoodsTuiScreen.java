package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import com.fasterxml.jackson.core.PrettyPrinter;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.*;

public class LooseGoodsTuiScreen extends GameTuiScreen {
    private List<Integer> storageIDs;

    public LooseGoodsTuiScreen() {
        super(new ArrayList<>(){{
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            spaceShipView.getMapStorages().values().stream()
                    .filter(storage -> java.util.Arrays.stream(storage.getGoods()).anyMatch(Objects::nonNull))
                    .forEach(storage -> add("(" + storage.getRow() + " " + storage.getCol() + ")"));
            add("Cancel");
            add("Done");
        }});
    }

    @Override
    protected String lineBeforeInput() {
        return "Choose a storage unit to discard the most expensive goods.";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapStorages().values().stream()
                .filter(storage -> java.util.Arrays.stream(storage.getGoods()).anyMatch(Objects::nonNull))
                .count();

        if (selected == num) {
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            return this;
        }

        if (selected == num + 1) {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 0, storageIDs));
        }

        spaceShipView.getMapStorages().entrySet().stream()
                .filter(entry -> java.util.Arrays.stream(entry.getValue().getGoods()).anyMatch(Objects::nonNull))
                .map(Map.Entry::getKey)
                .skip(selected)
                .findFirst()
                .ifPresent(storageIDs::add);

        return new LooseGoodsTuiScreen();
    }
}
