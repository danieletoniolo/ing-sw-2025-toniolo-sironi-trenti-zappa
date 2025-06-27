package it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnCards;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen that allows the player to choose which group of ship fragments to keep
 * when their ship is fragmented. Displays the available fragment groups and
 * handles the selection and communication with the server.
 */
public class ChooseFragmentsCards extends CardsGame {

    /**
     * Constructs the ChooseFragmentsCards screen, initializing the list of
     * fragment groups to display and setting the initial message.
     */
    public ChooseFragmentsCards() {
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
     * Returns the label to display before the input line.
     *
     * @return the string "Fragments:"
     */
    @Override
    protected String lineBeforeInput() {
        return "Fragments:";
    }

    /**
     * Handles the logic for setting the new screen after the player makes a selection.
     * Sends the fragment choice to the server, handles errors, and ends the turn if successful.
     *
     * @return the next screen to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        if (selected >= 0 && selected < MiniModel.getInstance().getClientPlayer().getShip().getFragments().size()) {
            // Send the request to choose the fragment group
            status = ChooseFragment.requester(Client.transceiver, new Object()).request(new ChooseFragment(MiniModel.getInstance().getUserID(), selected));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            // If the request was successful, end the turn
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            if (nextScreen == null) {
                return new NotClientTurnCards();
            }
            return nextScreen;
        }

        return this;
    }
}
