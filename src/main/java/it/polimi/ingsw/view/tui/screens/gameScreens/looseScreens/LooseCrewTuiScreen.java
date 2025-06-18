package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.event.game.clientToServer.spaceship.ManageCrewMember;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LooseCrewTuiScreen extends GameTuiScreen {
    private static List<Integer> cabinIDs;
    private static boolean reset;
    private TuiScreenView nextScreen;

    public LooseCrewTuiScreen() {
        super(new ArrayList<>(){{
            if (!reset) {
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
            }
            if (!reset) reset = true;
            addAll(
                    spaceShipView.getMapCabins().values().stream()
                            .filter(cabin -> cabin.getCrewNumber() != 0)
                            .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                            .toList()
            );
            add("Cancel");
            add("Done");
        }});
        if (cabinIDs == null) {
            cabinIDs = new ArrayList<>();
        }
    }

    private void destroyStatic() {
        cabinIDs = null;
        reset = false;
        setMessage(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a cabin to remove a crew member from";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) spaceShipView.getMapCabins().values().stream()
                .filter(cabin -> cabin.getCrewNumber() != 0)
                .count();

        if (selected == num) {
            destroyStatic();
            return new LooseCrewTuiScreen();
        }

        if (selected == num + 1) {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object())
                    .request(new SetPenaltyLoss(MiniModel.getInstance().getUserID(), 2, cabinIDs));
            destroyStatic();
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return new LooseCrewTuiScreen();
            }
            spaceShipView = clientPlayer.getShip();
            return nextScreen;
        }

        spaceShipView.getMapCabins().entrySet().stream()
                .filter(entry -> entry.getValue().getCrewNumber() != 0)
                .skip(selected)
                .findFirst()
                .ifPresent(entry -> {
                    cabinIDs.add(entry.getKey());
                    entry.getValue().setCrewNumber(entry.getValue().getCrewNumber() - 1);
                });

        StringBuilder line = new StringBuilder();
        for (Integer integer : cabinIDs) {
            line.append(spaceShipView.getMapCabins().get(integer).getCrewType().drawTui()).append("(").append(spaceShipView.getMapCabins().get(integer).getRow()).append(" ").append(spaceShipView.getMapCabins().get(integer).getCol()).append(") ");
        }
        setMessage("You are loosing a crew member from " + line);
        return new LooseCrewTuiScreen();
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
