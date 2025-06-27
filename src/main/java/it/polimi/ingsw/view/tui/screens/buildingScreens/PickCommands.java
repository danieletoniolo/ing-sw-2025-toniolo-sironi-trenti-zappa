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

/**
 * The PickCommands class represents a screen in the TUI where the user can choose
 * different commands for picking components during the building phase.
 * It extends the Building class and provides options for picking hidden components,
 * picking from the pile, picking the last component from the spaceship, picking from
 * the reserved pile, or going back.
 */
public class PickCommands extends Building {

    /**
     * Constructs a PickCommands screen with a predefined list of pick options.
     */
    public PickCommands() {
        super(new ArrayList<>(){{
            add("Pick an hidden component");
            add("Pick from the pile");
            add("Pick last component put on the spaceship");
            add("Pick from the reserved pile");
            add("Back");
        }});
    }

    /**
     * Sets the new screen based on the user's selection.
     * Handles the logic for each pick command, including requesting actions from the server
     * and handling possible errors.
     *
     * @return the next TuiScreenView to display, or this if an error occurs.
     */
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
