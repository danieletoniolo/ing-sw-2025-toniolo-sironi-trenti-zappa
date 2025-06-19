package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToBoard;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToReserve;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class PutCommands extends Building {

    public PutCommands() {
        super(new ArrayList<>(){{
            add("Put the tile on spaceship");
            add("Put the tile into the pile");
            add("Put the tile into the reserved pile");
            add("Back");
        }});
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        if (selected == 0) {
            return new RowAndCol(0);
        }

        if (selected == 1) {
            status = PlaceTileToBoard.requester(Client.transceiver, new Object()).request(new PlaceTileToBoard(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                TuiScreenView newScreen = new MainBuilding();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        if (selected == 2) {
            status = PlaceTileToReserve.requester(Client.transceiver, new Object()).request(new PlaceTileToReserve(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                TuiScreenView newScreen = new MainBuilding();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        return new MainBuilding();
    }
}
