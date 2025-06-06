package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromReserve;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromSpaceship;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.PickedTileFromReserve;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class PickCommandsTuiScreen extends BuildingTuiScreen {

    public PickCommandsTuiScreen() {
        super(new ArrayList<>(){{
            add("Pick an hidden component");
            add("Pick from the pile");
            add("Pick last component put on the spaceship");
            add("Pick from the reserved pile");
            add("Back");
        }});
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        if (selected == 0) {
            status = PickTileFromBoard.requester(Client.transceiver, new Object()).request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), -1));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
            }
        }

        if (selected == 1) {
            return new RowAndColTuiScreen(1);
        }

        if (selected == 2) {
            status = PickTileFromSpaceship.requester(Client.transceiver, new Object()).request(new PickTileFromSpaceship(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
            }
        }

        if (selected == 3) {
            return new PickReservedTuiScreen();
        }

        return new MainCommandsTuiScreen();
    }
}
