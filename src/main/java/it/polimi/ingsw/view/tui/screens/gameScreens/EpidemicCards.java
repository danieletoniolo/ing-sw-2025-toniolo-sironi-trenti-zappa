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
 * Represents the Epidemic Cards screen in the TUI game interface.
 * This screen is shown when a terrible disease event occurs on the ship.
 * It allows the player to handle the epidemic event and possibly end their turn.
 */
public class EpidemicCards extends CardsGame {

    /**
     * Constructs the EpidemicCards screen with a specific message and option.
     */
    public EpidemicCards() {
        super(List.of("Cut off infected crew members"));
        setMessage("A terrible disease is on the ship, be careful!");
    }

    /**
     * Handles the logic for setting the new screen when the player interacts with the epidemic card.
     * If the player selects the first option, it attempts to end the turn and handles any errors.
     *
     * @return the next TuiScreenView to display, or this screen if an error occurs.
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
