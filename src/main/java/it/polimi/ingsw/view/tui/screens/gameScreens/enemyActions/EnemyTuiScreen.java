package it.polimi.ingsw.view.tui.screens.gameScreens.enemyActions;

import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions.ChooseDoubleCannonsTuiScreen;

import java.util.List;

public class EnemyTuiScreen extends GameTuiScreen {

    public EnemyTuiScreen() {
        super(List.of("Active cannons", "Surrender"));
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
                    this.setMessage(((Pota) status).errorMessage());
                    return this;
                }
                spaceShipView = clientPlayer.getShip().clone();
                return new ChooseDoubleCannonsTuiScreen(this);
            case 1:
                //status = EndTurn.requester(Client.transceiver, )
                break;
        }

        return this;
    }
}
