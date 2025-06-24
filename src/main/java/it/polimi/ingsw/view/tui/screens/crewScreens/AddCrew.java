package it.polimi.ingsw.view.tui.screens.crewScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ManageCrewMember;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.tui.screens.ModifyCrew;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;
import java.util.Map;

public class AddCrew extends ModifyCrew {
    private final int value;

    public AddCrew(int value) {
        super(new ArrayList<>(){{
            if (value == 3) {
                addAll(MiniModel.getInstance().getClientPlayer().getShip().getMapCabins().values().stream()
                        .filter(cabin -> cabin.getCrewNumber() != 0)
                        .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                        .toList());
            }
            else  {
                addAll(MiniModel.getInstance().getClientPlayer().getShip().getMapCabins().values().stream()
                        .filter(cabin -> cabin.getCrewNumber() == 0)
                        .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                        .toList());
            }
            add("Back");
        }});

        this.value = value;
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = (int) MiniModel.getInstance().getClientPlayer().getShip().getMapCabins().values().stream()
                    .filter(cabin -> (value == 3) == (cabin.getCrewNumber() != 0))
                    .count();

        if (selected == num) {
            return new MainCrew();
        }

        if (selected < 0 || selected >= spaceShipView.getMapCabins().size()) {
            return this;
        }

        int ID;
        if (value == 3) { // If value is 3, we want to remove crew members on an ID cabin
            ID = spaceShipView.getMapCabins().entrySet().stream()
                    .filter(entry -> entry.getValue().getCrewNumber() != 0)
                    .skip(selected)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(-1);
        }
        else {
            ID = spaceShipView.getMapCabins().entrySet().stream()
                    .filter(entry -> entry.getValue().getCrewNumber() == 0)
                    .skip(selected)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(-1);
        }

        CabinView cabin = spaceShipView.getMapCabins().get(ID);

        int mode = 0;
        int type;
        if (value == 3) {
            mode = 1;
            type = cabin.hasBrownAlien() ? 1 : cabin.hasPurpleAlien() ? 2 : 0;
        }
        else {
            type = value;
        }

        StatusEvent status;
        // Send event to the server to add or remove a crew member
        status = ManageCrewMember.requester(Client.transceiver, new Object()).request(new ManageCrewMember(MiniModel.getInstance().getUserID(), mode, type, ID));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
        }

        return new MainCrew();
    }
}
