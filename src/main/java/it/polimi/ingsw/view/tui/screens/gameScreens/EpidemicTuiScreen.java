package it.polimi.ingsw.view.tui.screens.gameScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.List;

public class EpidemicTuiScreen extends GameTuiScreen {
    private TuiScreenView nextScreen;

    public EpidemicTuiScreen() {
        super(List.of("Go on"));
        setMessage("A terrible disease is on the ship, be careful!");
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
            if (nextScreen == null) {
                return new NotClientTurnTuiScreen();
            }
             return nextScreen;
        }
        return this;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextScreen = nextScreen;
    }
}
