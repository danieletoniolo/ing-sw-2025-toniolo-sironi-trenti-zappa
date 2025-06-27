package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.rotateTile.RotateTile;
import it.polimi.ingsw.event.game.clientToServer.timer.FlipTimer;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.ComponentTypeView;
import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.ChoosePosition;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.ArrayList;

/**
 * MainBuilding is a screen in the TUI that manages the main building phase of the game.
 * It extends the Building class and provides options such as picking, putting, rotating tiles,
 * picking a deck, flipping the timer, and placing a marker, depending on the game state.
 */
public class MainBuilding extends Building {

    /**
     * Constructs the MainBuilding screen, initializing the available actions
     * based on the current game state (e.g., board level, timer flips).
     */
    public MainBuilding() {
        super(new ArrayList<>(){{
            add("Pick tile");
            add("Put tile");
            add("Rotate tile");
            if (MiniModel.getInstance().getBoardView().getLevel().equals(LevelView.SECOND)) {
                add("Pick deck");
                if (MiniModel.getInstance().getTimerView().getNumberOfFlips() < MiniModel.getInstance().getTimerView().getTotalFlips() - 1) {
                    add("Flip timer");
                }
            }
            add("Place marker");
        }});
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return the TuiScreens enum value representing MainBuilding
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.MainBuilding;
    }

    /**
     * Sets and returns the new screen based on the user's selection and the current game state.
     * Handles actions such as picking, putting, rotating tiles, picking a deck, flipping the timer,
     * and placing a marker. Also manages error messages and cheat screen access.
     *
     * @return the next TuiScreenView to display, or this if no transition occurs
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == -4 && MiniModel.getInstance().getClientPlayer().getHand().getType().equals(ComponentTypeView.GENERIC)) {
            return new CheatShipScreen();
        }

        if (selected == 0) {
            return new PickCommands();
        }

        if (selected == 1) {
            return new PutCommands();
        }

        StatusEvent status;
        if (selected == 2) {
            // Rotate the tile
            status = RotateTile.requester(Client.transceiver, new Object())
                    .request(new RotateTile(MiniModel.getInstance().getUserID(), clientPlayer.getHand().getID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
            }
            return this;
        }

        if (MiniModel.getInstance().getBoardView().getLevel().equals(LevelView.SECOND)) {
            if (selected == 3) {
                return new DeckCommands();
            }
            if (MiniModel.getInstance().getTimerView().getNumberOfFlips() < MiniModel.getInstance().getTimerView().getTotalFlips() - 1) {
                if (selected == 4) {
                    // Flip the timer
                    status = FlipTimer.requester(Client.transceiver, new Object()).request(new FlipTimer(MiniModel.getInstance().getUserID()));
                    if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                        setMessage(((Pota) status).errorMessage());
                    }
                    return this;
                }
                selected -= 2;
            }
            else {
                selected -= 1;
            }
        }

        if (selected == 3) {
            return new ChoosePosition(true);
        }

        return this;
    }
}
