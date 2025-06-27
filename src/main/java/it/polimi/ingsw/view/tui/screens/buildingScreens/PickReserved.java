package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromReserve;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * Screen for picking a reserved tile in the building phase.
 * Displays the list of reserved tiles and allows the user to select one or go back.
 */
public class PickReserved extends Building {

    /**
     * Constructs the PickReserved screen, initializing the selectable options
     * with the reserved tiles and a "Back" option.
     */
    public PickReserved() {
        super(new ArrayList<>(){{
            for (int i = 0; i < MiniModel.getInstance().getClientPlayer().getShip().getDiscardReservedPile().getReserved().size(); i++) {
                add((i + 1) + "");
            }
            add("Back");
        }});
    }

    /**
     * Handles the logic for setting the new screen after a selection is made.
     * If "Back" is selected, returns to the PickCommands screen.
     * If a reserved tile is selected, sends a request to pick it and handles the response.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int num = MiniModel.getInstance().getClientPlayer().getShip().getDiscardReservedPile().getReserved().size();
        if (selected == num) {
            return new PickCommands();
        }

        int ID = MiniModel.getInstance().getClientPlayer().getShip().getDiscardReservedPile().getReserved().stream()
                .skip(selected)
                .map(ComponentView::getID)
                .findFirst()
                .orElse(-1);

        if (ID != -1) {
            // Send the request to pick a tile from the reserve
            StatusEvent status = PickTileFromReserve.requester(Client.transceiver, new Object()).request(new PickTileFromReserve(MiniModel.getInstance().getUserID(), ID));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                TuiScreenView newScreen = new MainBuilding();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        return new MainBuilding();
    }
}
