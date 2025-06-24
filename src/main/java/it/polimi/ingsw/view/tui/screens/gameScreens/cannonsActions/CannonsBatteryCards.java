package it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions;

import it.polimi.ingsw.event.game.clientToServer.energyUse.UseCannons;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class CannonsBatteryCards extends ManagerCannonsCards {

    public CannonsBatteryCards() {
        super(new ArrayList<>(){{
            if (batteriesIDs == null) {
                batteriesIDs = new ArrayList<>();
            }
            if (cannonsIDs.size() > batteriesIDs.size()) {
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

        if (cannonsIDs.size() == batteriesIDs.size()) num = 0;

        if (selected == num) {
            destroyStatics();
            setMessage(null);
            return new ChooseDoubleCannonsCards();
        }

        if (selected == num + 1) {
            StatusEvent status;
            // Send the request to use cannons and batteries
            status = UseCannons.requester(Client.transceiver, new Object()).request(new UseCannons(MiniModel.getInstance().getUserID(), cannonsIDs, batteriesIDs));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new ChooseDoubleCannonsCards();
            }
            // Send the request to end the turn
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return new ChooseDoubleCannonsCards();
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
        for (Integer ID : cannonsIDs) {
            line.append("(").append(spaceShipView.getMapDoubleCannons().get(ID).getRow()).append(" ").append(spaceShipView.getMapDoubleCannons().get(ID).getCol()).append(") ");
        }
        line.append("with ");
        for (Integer ID : batteriesIDs) {
            line.append("(").append(spaceShipView.getMapBatteries().get(ID).getRow()).append(" ").append(spaceShipView.getMapBatteries().get(ID).getCol()).append(") ");
        }

        TuiScreenView newScreen = new CannonsBatteryCards();
        newScreen.setMessage("You are activating " + line);
        return newScreen;
    }
}
