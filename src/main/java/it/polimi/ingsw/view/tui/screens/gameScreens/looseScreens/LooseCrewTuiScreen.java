package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.event.game.clientToServer.spaceship.ManageCrewMember;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class LooseCrewTuiScreen extends GameTuiScreen {

    public LooseCrewTuiScreen() {
        super(new ArrayList<>(){{
            addAll(
                    spaceShipView.getMapCabins().values().stream()
                            .filter(cabin -> cabin.getCrewNumber() != 0)
                            .map(cabin -> "(" + cabin.getRow() + " " + cabin.getCol() + ")")
                            .toList()
            );
        }});
    }


    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int ID = spaceShipView.getMapCabins().keySet().stream()
                .skip(selected)
                .findFirst()
                .orElse(-1);

        CabinView cabin = spaceShipView.getMapCabins().get(ID);
        int type = cabin.hasBrownAlien() ? 1 : cabin.hasPurpleAlien() ? 2 : 0;

        StatusEvent status = ManageCrewMember.requester(Client.transceiver, new Object())
                .request(new ManageCrewMember(MiniModel.getInstance().getUserID(), 1, type, ID));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
        }

        return this;
    }
}
