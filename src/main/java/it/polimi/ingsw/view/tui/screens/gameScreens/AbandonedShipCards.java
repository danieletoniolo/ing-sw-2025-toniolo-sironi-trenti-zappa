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

public class AbandonedShipCards extends CardsGame {

    public AbandonedShipCards() {
        super(List.of("Accept", "Refuse"));
    }

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

    @Override
    public String lineBeforeInput() {
        return "You have reached an abandoned ship, wanna look for some crew?";
    }
}
