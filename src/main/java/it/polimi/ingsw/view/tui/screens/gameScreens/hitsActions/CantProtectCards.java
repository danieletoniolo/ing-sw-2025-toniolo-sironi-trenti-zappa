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
import java.util.List;

/**
 * Represents a game screen where the player cannot protect their cards.
 * This screen is shown when the player is unable to use a shield to defend their cards.
 * It handles the logic for attempting to use a shield and, if not possible, ends the player's turn.
 */
public class CantProtectCards extends CardsGame {

    /**
     * Constructs the CantProtectCards screen with a default message.
     */
    public CantProtectCards() {
        super(new ArrayList<>() {{
            add("Ready for the impact");
        }});
    }

    /**
     * Sets the new screen for the TUI.
     * Attempts to use a shield with an invalid battery index (-1).
     * If the shield cannot be used, displays the error message.
     * Otherwise, ends the player's turn and handles any errors.
     * Returns the next appropriate screen.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        List<Integer> batteries = new ArrayList<>();
        batteries.add(-1);
        StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(MiniModel.getInstance().getUserID(), batteries));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        // Player is ready for the next hit, so we end the turn
        status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return nextScreen == null ? new NotClientTurnCards() : nextScreen;
    }
}
