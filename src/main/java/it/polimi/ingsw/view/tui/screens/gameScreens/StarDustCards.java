package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.List;

public class StarDustCards extends CardsGame {

    public StarDustCards() {
        super(List.of("Move marker"));
        setMessage("Sensor report: high-density star dust incoming.");
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }
            return nextScreen;
        }
        return this;
    }
}
