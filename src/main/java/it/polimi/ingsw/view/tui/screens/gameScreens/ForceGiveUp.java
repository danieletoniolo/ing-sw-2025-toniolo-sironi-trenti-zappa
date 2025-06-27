package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.GiveUp;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.ClosingProgram;
import it.polimi.ingsw.view.tui.screens.OtherPlayer;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

/**
 * The ForceGiveUp class represents a screen in the TUI where a player is forced to give up.
 * It extends CardsGame and manages the logic for handling forced give up actions,
 * including updating the game state and handling user selections.
 */
public class ForceGiveUp extends CardsGame {

    /**
     * Constructs a ForceGiveUp screen with no initial parameters.
     */
    public ForceGiveUp() {
        super(null);
    }

    /**
     * Handles the logic for setting the new screen based on the user's selection.
     * Depending on the selected option, it may:
     * - Show another player's information,
     * - Force the current player to give up and end their turn,
     * - Close the program,
     * - Or remain on the current screen if the selection is invalid.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 2) && (selected >= options.size() - 2 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 2);

            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 2) {
            // Force give up
            StatusEvent status;
            status = GiveUp.requester(Client.transceiver, new Object()).request(new GiveUp(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            // Request to end the turn after giving up
            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            return nextScreen == null ? new NotClientTurnCards() : nextScreen;

        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        if (selected < 0 || selected >= options.size()) {
            return this;
        }

        return this;
    }
}
