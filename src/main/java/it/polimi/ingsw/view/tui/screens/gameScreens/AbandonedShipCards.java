package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.looseScreens.LooseCrewCards;

import java.util.List;

/**
 * Represents the screen for handling the "Abandoned Ship" card event in the TUI.
 * Allows the player to choose whether to accept or refuse searching for crew on an abandoned ship.
 * Extends {@link CardsGame} to provide card selection functionality.
 */
public class AbandonedShipCards extends CardsGame {

    /**
     * Constructs the AbandonedShipCards screen with the available options.
     * The options are "Accept" and "Refuse".
     */
    public AbandonedShipCards() {
        super(List.of("Accept", "Refuse"));
    }

    /**
     * Handles the logic for setting the new screen based on the player's selection.
     * If "Accept" is chosen, attempts to play the card and handles possible errors.
     * If "Refuse" is chosen, ends the turn and handles possible errors.
     *
     * @return the next {@link TuiScreenView} to display, or this screen if an error occurs.
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        switch (selected) {
            case 0:
                // Request to play the card
                status = Play.requester(Client.transceiver, new Object()).request(new Play(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return new LooseCrewCards();
            case 1:
                // Refuse the card and end the turn
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return nextScreen == null ? new NotClientTurnCards() : nextScreen;

        }

        return this;
    }

    /**
     * Provides the message displayed before the user input prompt.
     *
     * @return a string describing the abandoned ship scenario.
     */
    @Override
    public String lineBeforeInput() {
        return "You have reached an abandoned ship, wanna look for some crew?";
    }
}
