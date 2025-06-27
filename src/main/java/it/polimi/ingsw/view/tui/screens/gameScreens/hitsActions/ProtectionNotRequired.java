package it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseShield;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnCards;

import java.util.ArrayList;

/**
 * Represents a game screen where protection is not required after a hit.
 * This screen is shown when the player is ready for the next impact and no shield or protection action is needed.
 * Extends {@link CardsGame}.
 */
public class ProtectionNotRequired extends CardsGame {

    /**
     * Constructs the ProtectionNotRequired screen with a default message.
     */
    public ProtectionNotRequired() {
        super(new ArrayList<>() {{
            add("Ready for the impact");
        }});
    }

    /**
     * Sets the new screen after the player is ready for the next hit.
     * If the turn ends successfully, proceeds to the next screen.
     * If there is an error, displays the error message and stays on the current screen.
     *
     * @return the next {@link TuiScreenView} to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        // Player is ready for the next hit, so we end the turn
        StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return nextScreen == null ? new NotClientTurnCards() : nextScreen;
    }
}
