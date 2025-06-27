package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.List;

/**
 * Represents the Star Dust Cards screen in the TUI during the game.
 * Extends {@link CardsGame} and manages the logic for the "Move marker" action
 * and the end turn request when high-density star dust is incoming.
 */
public class StarDustCards extends CardsGame {

    /**
     * Constructs a new StarDustCards screen with a predefined message and option.
     */
    public StarDustCards() {
        super(List.of("Move marker"));
        setMessage("Sensor report: high-density star dust incoming.");
    }

    /**
     * Handles the transition to a new screen based on the user's selection.
     * If the "Move marker" option is selected, it sends an end turn request.
     * Displays an error message if the request fails, or proceeds to the next screen if successful.
     *
     * @return the next {@link TuiScreenView} to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            // Request to end the turn
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }
            return nextScreen == null ? new NotClientTurnCards() : nextScreen;

        }
        return this;
    }
}
