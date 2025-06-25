package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromSpaceship;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class PickCommands extends Building {

    public PickCommands() {
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
            // Pick a hidden component from the board
            status = PickTileFromBoard.requester(Client.transceiver, new Object()).request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), -1));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }
        }

        if (selected == 1) {
            return new RowAndCol(1);
        }

        if (selected == 2) {
            // Pick the last component put on the spaceship
            status = PickTileFromSpaceship.requester(Client.transceiver, new Object()).request(new PickTileFromSpaceship(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }
        }

        if (selected == 3) {
            return new PickReserved();
        }

        return new MainBuilding();
    }
}
