package it.polimi.ingsw.view.tui.screens.gameScreens.planetsActions;

import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnTuiScreen;

import java.util.List;

public class PlanetsTuiScreen extends GameTuiScreen {
    private TuiScreenView nextScreen;

    public PlanetsTuiScreen() {
        super(List.of("Accept", "Refuse"));
        setMessage("You can now play on the planets, choose what to do!");
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        StatusEvent status;
        switch (selected) {
            case 0:
                status = Play.requester(Client.transceiver, new Object()).request(new Play(MiniModel.getInstance().getUserID()));
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
                }
                setMessage(null);
                return new SelectPlanetTuiScreen(this);
            case 1:
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    return this;
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
