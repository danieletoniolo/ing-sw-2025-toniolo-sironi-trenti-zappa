package it.polimi.ingsw.view.tui.screens.gameScreens.engineActions;

import it.polimi.ingsw.event.game.clientToServer.energyUse.UseEngines;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class EngineBatteryCards extends ManagerEnginesCards {
    public EngineBatteryCards() {
        super(new ArrayList<>(){{
            if (batteriesIDs == null) batteriesIDs = new ArrayList<>();

            if (enginesIDs.size() > batteriesIDs.size()) {
                spaceShipView.getMapBatteries().forEach(
                        (_, value) -> {
                            if (value.getNumberOfBatteries() != 0) {
                                add("Use battery " + "(" + value.getRow() + " " + value.getCol() + ")");
                            }
                        }
                );
            }
            add("Cancel");
            add("Done");
        }});
    }

    @Override
    protected String lineBeforeInput() {
        return "Select batteries to use";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapBatteries().values().stream()
                .filter(battery -> battery.getNumberOfBatteries() != 0)
                .count();

        if (enginesIDs.size() == batteriesIDs.size()) num = 0;

        if (selected == num) {
            destroyStatics();
            setMessage(null);
            return new ChooseDoubleEngineCards();
        }

        if (selected == num + 1) {
            StatusEvent status;
            // Send the request to use engines and batteries
            status = UseEngines.requester(Client.transceiver, new Object()).request(new UseEngines(MiniModel.getInstance().getUserID(), enginesIDs, batteriesIDs));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                destroyStatics();
                return new ChooseDoubleEngineCards();
            }
            // Send the request to end the turn
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                destroyStatics();
                return new ChooseDoubleEngineCards();
            }
            destroyStatics();
            return nextScreen;
        }

        spaceShipView.getMapBatteries().entrySet().stream()
                .filter(entry -> entry.getValue().getNumberOfBatteries() != 0)
                .skip(selected)
                .findFirst()
                        .ifPresent(entry -> {
                            batteriesIDs.add(entry.getKey());
                            entry.getValue().setNumberOfBatteries(entry.getValue().getNumberOfBatteries() - 1);
                        });


        StringBuilder line = new StringBuilder();
        for (Integer ID : enginesIDs) {
            line.append("(").append(spaceShipView.getMapDoubleEngines().get(ID).getRow()).append(" ").append(spaceShipView.getMapDoubleEngines().get(ID).getCol()).append(") ");
        }
        line.append("with ");
        for (Integer ID : batteriesIDs) {
            line.append("(").append(spaceShipView.getMapBatteries().get(ID).getRow()).append(" ").append(spaceShipView.getMapBatteries().get(ID).getCol()).append(") ");
        }

        setMessage("You are activating " + line);
        return new EngineBatteryCards();
    }
}
