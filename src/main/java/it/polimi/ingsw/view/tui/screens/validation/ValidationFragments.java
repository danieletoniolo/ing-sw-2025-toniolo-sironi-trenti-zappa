package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.Validation;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * ValidationFragments is a screen that handles the validation process when a player's ship is fragmented.
 * It displays the available groups of ship fragments and allows the player to choose which group to keep.
 * After the selection, it sends the appropriate requests to the server and manages error handling and turn ending.
 */
public class ValidationFragments extends Validation {

    /**
     * Constructs the ValidationFragments screen.
     * Initializes the list of fragment groups to display and sets the initial message for the user.
     */
    public ValidationFragments() {
        super(new ArrayList<>(){{
            int i = 1;
            for (List<Pair<Integer, Integer>> group : MiniModel.getInstance().getClientPlayer().getShip().getFragments()) {
                StringBuilder line = new StringBuilder();
                line.append("Group ").append(i).append(": ");
                for (Pair<Integer, Integer> tile : group) {
                    line.append("(").append(tile.getValue0() + 1).append(" ").append(tile.getValue1() + 1).append(") ");
                }
                add(line.toString());
                i++;
            }
        }});
        setMessage("Your ship is fragmented, you have to choose which group of components to keep");
    }

    /**
     * Handles the logic for setting the new screen after the user makes a selection.
     * Sends the fragment choice and end turn requests to the server, manages errors, and transitions to the next screen.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        if (selected >= 0 && selected < MiniModel.getInstance().getClientPlayer().getShip().getFragments().size()) {
            // Send the choose fragment request to the server
            status = ChooseFragment.requester(Client.transceiver, new Object()).request(new ChooseFragment(MiniModel.getInstance().getUserID(), selected));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            // End the turn after choosing fragments
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }
            destroyStatics();
            return nextState == null ? new WaitingValidation() : nextState;
        }

        return this;
    }

    /**
     * Returns the label to display before the input prompt.
     *
     * @return a string label for the fragments section
     */
    @Override
    protected String lineBeforeInput() {
        return "Fragments:";
    }
}
