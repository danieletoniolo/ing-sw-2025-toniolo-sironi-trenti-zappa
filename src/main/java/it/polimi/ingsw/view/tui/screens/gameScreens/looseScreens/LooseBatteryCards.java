package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;
import java.util.List;

public class LooseBatteryCards extends CardsGame {
    private static boolean reset;
    private static List<Integer> batteryIDs;
    private final TuiScreenView nextScreen;

    public LooseBatteryCards(TuiScreenView nextScreen) {
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
        this.nextScreen = nextScreen;
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a battery where to drop energy from:";
    }

    private void destroyStatic() {
        batteryIDs = null;
        reset = false;
        setMessage(null);
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapBatteries().values().stream()
                .filter(value -> value.getNumberOfBatteries() != 0)
                .count();

        if (selected == num) {
            destroyStatic();
            return new LooseBatteryCards(nextScreen);
        }

        if (selected == num + 1) {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 1, batteryIDs));
            destroyStatic();
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return new LooseBatteryCards(nextScreen);
            }
            spaceShipView = clientPlayer.getShip();
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
        return new LooseBatteryCards(nextScreen);
    }
}
