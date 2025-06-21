package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;
import java.util.List;

public class LooseBatteryCards extends CardsGame {
    private static boolean reset;
    private static List<Integer> batteryIDs;

    public LooseBatteryCards() {
        super(new ArrayList<>(){{
            if (!reset) {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            }
            if (!reset) reset = true;
            spaceShipView.getMapBatteries().values().stream()
                    .filter(value -> value.getNumberOfBatteries() != 0)
                    .forEach(value -> add("(" + value.getRow() + " " + value.getCol() + ")"));
            add("Cancel");
            add("Done");
        }});
        if (batteryIDs == null) {
            batteryIDs = new ArrayList<>();
        }
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a battery where to drop energy from:";
    }

    public static void destroyStatics() {
        batteryIDs = null;
        reset = false;
        PlayerDataView player = MiniModel.getInstance().getClientPlayer();
        spaceShipView = player == null ? null : player.getShip();
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapBatteries().values().stream()
                .filter(value -> value.getNumberOfBatteries() != 0)
                .count();

        if (selected == num) {
            destroyStatics();
            return new LooseBatteryCards();
        }

        if (selected == num + 1) {
            StatusEvent status;
            // Send the loosing batteryIDs to the server
            status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 1, batteryIDs));
            destroyStatics();
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseBatteryCards();
            }
            // After selected loosing batteries end the turn
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new LooseBatteryCards();
            }
            return nextScreen;
        }

        spaceShipView.getMapBatteries().entrySet().stream()
                .filter(entry -> entry.getValue().getNumberOfBatteries() != 0)
                .skip(selected)
                .findFirst()
                .ifPresent(entry -> {
                    batteryIDs.add(entry.getKey());
                    entry.getValue().setNumberOfBatteries(entry.getValue().getNumberOfBatteries() - 1);
                });

        StringBuilder line = new StringBuilder();
        for (Integer integer : batteryIDs) {
            line.append("(").append(spaceShipView.getMapBatteries().get(integer).getRow()).append(" ").append(spaceShipView.getMapBatteries().get(integer).getCol()).append(") ");
        }
        setMessage("You are loosing batteries from " + line);
        return new LooseBatteryCards();
    }
}
