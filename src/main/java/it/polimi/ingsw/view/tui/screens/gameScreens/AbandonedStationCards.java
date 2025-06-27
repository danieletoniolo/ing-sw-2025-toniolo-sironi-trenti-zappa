package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.goodsActions.MenuGoodsCards;

import java.util.List;

/**
 * Represents the screen for handling cards found at an abandoned station in the TUI.
 * Allows the player to accept or refuse the card, triggering the appropriate game actions.
 */
public class AbandonedStationCards extends CardsGame {

    /**
     * Constructs the AbandonedStationCards screen with options to accept or refuse.
     */
    public AbandonedStationCards() {
        super(List.of("Accept", "Refuse"));
    }

    /**
     * Sets the new screen based on the player's selection.
     * If "Accept" is chosen, attempts to play the card.
     * If "Refuse" is chosen, ends the player's turn.
     * Handles error messages if the action fails.
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
                return new MenuGoodsCards();
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

    /**
     * Returns the message to display before the input prompt.
     *
     * @return the prompt message
     */
    @Override
    public String lineBeforeInput() {
        return "You have reached an abandoned station, wanna look for some goods?";
    }
}
