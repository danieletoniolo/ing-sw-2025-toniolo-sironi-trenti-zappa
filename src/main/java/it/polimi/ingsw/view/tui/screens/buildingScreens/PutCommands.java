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

/**
 * The PutCommands class represents a screen in the TUI where the user can choose
 * how to place a tile (on the spaceship, into the pile, or into the reserved pile).
 * It extends the Building class and manages the logic for handling user selection
 * and sending the appropriate requests to the server.
 */
public class PutCommands extends Building {

    /**
     * Constructs a PutCommands screen with the available placement options.
     */
    public PutCommands() {
        super(new ArrayList<>(){{
            add("Put the tile on spaceship");
            add("Put the tile into the pile");
            add("Put the tile into the reserved pile");
            add("Back");
        }});
    }

    /**
     * Handles the logic for setting the new screen based on the user's selection.
     * Depending on the selected option, it may send a request to the server to place
     * the tile or navigate to another screen.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        if (selected == 0) {
            return new RowAndCol(0);
        }

        if (selected == 1) {
            // Send the request to place the tile on the board
            status = PlaceTileToBoard.requester(Client.transceiver, new Object()).request(new PlaceTileToBoard(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                TuiScreenView newScreen = new MainBuilding();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        if (selected == 2) {
            // Place the tile into the reserved pile
            status = PlaceTileToReserve.requester(Client.transceiver, new Object()).request(new PlaceTileToReserve(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                TuiScreenView newScreen = new MainBuilding();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        return new MainBuilding();
    }
}
