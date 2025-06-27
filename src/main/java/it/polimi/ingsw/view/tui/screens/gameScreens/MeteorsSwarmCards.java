package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.dice.RollDice;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

/**
 * Represents the screen for the "Meteors Swarm" event in the TUI game.
 * Extends {@link CardsGame} and manages the interaction when a meteor swarm occurs,
 * allowing the player to roll dice and end their turn.
 */
public class MeteorsSwarmCards extends CardsGame {

    /**
     * Constructs a new MeteorsSwarmCards screen with the appropriate options and message.
     */
    public MeteorsSwarmCards() {
        super(new ArrayList<>(){{
            add("Roll dice");
        }});
        setMessage("A meteor swarn is coming! Good luck!");
    }

    /**
     * Handles the logic for setting the new screen after the player interacts with the options.
     * If the player chooses to roll the dice, it sends the roll request and then ends the turn.
     * Handles error messages and transitions to the next screen accordingly.
     *
     * @return the next {@link TuiScreenView} to display, or this screen if an error occurs.
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            StatusEvent status;
            // Request the dice roll
            status = RollDice.requester(Client.transceiver, new Object()).request(new RollDice(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            // After rolling the dice, we need to end the turn
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            return nextScreen == null ? new NotClientTurnCards() : nextScreen;
        }

        return this;
    }
}
