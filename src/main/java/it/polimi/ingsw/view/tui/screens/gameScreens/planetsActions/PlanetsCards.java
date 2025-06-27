package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.List;

/**
 * The PlanetsCards class represents the screen where the player can choose to play or refuse a card
 * during the planets phase of the game. It extends CardsGame and provides options to accept or refuse
 * the card, handling the corresponding actions and transitions to the next screen.
 */
public class PlanetsCards extends CardsGame {

    /**
     * Constructs a new PlanetsCards screen with options to accept or refuse the card.
     */
    public PlanetsCards() {
        super(List.of("Accept", "Refuse"));
        setMessage("You can now play on the planets, choose what to do!");
    }

    /**
     * Handles the logic for setting the new screen based on the player's selection.
     * If the player accepts, it attempts to play the card and transitions to SelectPlanetCards.
     * If the player refuses, it ends the turn and transitions to the next screen.
     * Displays error messages if the action fails.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        switch (selected) {
            case 0:
                // Accept the card and play it
                status = Play.requester(Client.transceiver, new Object()).request(new Play(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return new SelectPlanetCards(this);
            case 1:
                // Refuse the card and end the turn
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                return nextScreen;
        }

        return this;
    }
}
