package it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions;

import it.polimi.ingsw.event.game.clientToServer.dice.RollDice;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.List;

/**
 * Represents the screen for rolling dice cards in the TUI game.
 * Handles the logic for rolling dice and ending the player's turn.
 */
public class RollDiceCards extends CardsGame {

    /**
     * Constructs a new RollDiceCards screen with the "Roll dice" option.
     */
    public RollDiceCards() {
        super(List.of("Roll dice"));
    }

    /**
     * Sets the new screen after the player chooses to roll the dice.
     * Handles the dice roll request and ends the player's turn.
     * Displays error messages if any operation fails.
     *
     * @return the next screen to display, or this screen if an error occurs
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        // Player roll the dice
        StatusEvent status = RollDice.requester(Client.transceiver, new Object()).request(new RollDice(MiniModel.getInstance().getUserID()));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        // End turn after rolling the dice
        status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return nextScreen;
    }
}
