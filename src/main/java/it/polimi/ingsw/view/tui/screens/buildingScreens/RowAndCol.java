package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToSpaceship;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import org.javatuples.Pair;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

/**
 * Represents a screen in the TUI for entering row and column coordinates.
 * Used for both placing a tile on the spaceship and picking a tile from the board,
 * depending on the value of {@code num}.
 */
public class RowAndCol extends Building {
    /** Stores the row and column entered by the user as a pair. */
    private Pair<Integer, Integer> rowAndCol;
    /** Determines the action: 0 for placing, 1 for picking a tile. */
    private final int num;

    /**
     * Constructs a RowAndCol screen with the specified action type.
     *
     * @param num 0 to place a tile, 1 to pick a tile
     */
    public RowAndCol(int num) {
        super(null);
        options.clear();
        options.add("");
        this.num = num;
    }

    /**
     * Reads the row and column coordinates from the user input using the provided parser.
     *
     * @param parser the parser to read user input
     */
    @Override
    public void readCommand(Parser parser) {
        rowAndCol = parser.getRowAndCol("Type coordinates (row col): ", totalLines);
    }

    /**
     * Sets the next screen based on the action type and the entered coordinates.
     * Handles both placing a tile on the spaceship and picking a tile from the board.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        if (num == 0) {
            // Request to place the tile on the spaceship
            StatusEvent status = PlaceTileToSpaceship.requester(Client.transceiver, new Object())
                    .request(new PlaceTileToSpaceship(MiniModel.getInstance().getUserID(), rowAndCol.getValue0() - 1, rowAndCol.getValue1() - 1));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                TuiScreenView newScreen = new MainBuilding();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        if (num == 1) {
            int ID = MiniModel.getInstance().getViewablePile().getViewableComponents().stream()
                    .skip(((long) rowAndCol.getValue0() * rowAndCol.getValue1()) - 1)
                    .map(ComponentView::getID)
                    .findFirst()
                    .orElse(-1);

            if (ID != -1) {
                // Send the request to pick the tile from the board
                StatusEvent status = PickTileFromBoard.requester(Client.transceiver, new Object())
                        .request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), ID));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    TuiScreenView newScreen = new MainBuilding();
                    newScreen.setMessage(((Pota) status).errorMessage());
                    return newScreen;
                }
            }
            else {
                setMessage("Not valid coordinates");
            }
        }

        return new MainBuilding();
    }
}
