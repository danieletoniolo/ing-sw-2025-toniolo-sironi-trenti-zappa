package it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions;

import it.polimi.ingsw.event.game.clientToServer.dice.RollDice;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.List;

public class RollDiceTuiScreen extends GameTuiScreen {

    public RollDiceTuiScreen() {
        super(List.of("Roll dice"));
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status = RollDice.requester(Client.transceiver, new Object()).request(new RollDice(MiniModel.getInstance().getUserID()));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
        }

        return this;
    }
}
