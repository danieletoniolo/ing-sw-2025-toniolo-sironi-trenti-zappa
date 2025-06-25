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

public class ForceGiveUp extends CardsGame {

    public ForceGiveUp() {
        super(null);
    }

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
