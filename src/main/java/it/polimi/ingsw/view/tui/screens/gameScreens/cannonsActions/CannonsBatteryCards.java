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
    private final TuiScreenView oldScreen;

    public CannonsBatteryCards(TuiScreenView oldScreen) {
        super(new ArrayList<>(){{
            if (batteriesIDs == null) {
                batteriesIDs = new ArrayList<>();
            }
            if (cannonsIDs.size() > batteriesIDs.size()) {
                spaceShipView.getMapBatteries().forEach(
                        (key, value) -> {
                            if (value.getNumberOfBatteries() != 0) {
                                add("Use battery " + "(" + value.getRow() + " " + value.getCol() + ")");
                            }
                        }
                );
            }
            add("Cancel");
            add("Done");
        }});
        this.oldScreen = oldScreen;
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
            oldScreen.setMessage(null);
            return oldScreen;
        }

        if (selected == num + 1) {
            StatusEvent status;
            status = UseCannons.requester(Client.transceiver, new Object()).request(new UseCannons(MiniModel.getInstance().getUserID(), cannonsIDs, batteriesIDs));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return oldScreen;
            }
            else {
                setMessage(null);
            }
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return oldScreen;
            }

            destroyStatics();
            return nextScreen; // This screen is set by the TuiManager in the notifyEnemyDefeat method
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

        TuiScreenView newScreen = new CannonsBatteryCards(oldScreen);
        newScreen.setMessage("You are activating " + line);
        return newScreen;
    }
}
