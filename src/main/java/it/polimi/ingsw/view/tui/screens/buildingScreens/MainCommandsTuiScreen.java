package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.rotateTile.RotateTile;
import it.polimi.ingsw.event.game.clientToServer.timer.FlipTimer;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.screens.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class MainCommandsTuiScreen extends BuildingTuiScreen {

    public MainCommandsTuiScreen() {
        super(new ArrayList<>(){{
            add("Pick tile");
            add("Put tile");
            add("Rotate tile");
            if (MiniModel.getInstance().getBoardView().getLevel().equals(LevelView.SECOND)) {
                add("Pick deck");
                if (MiniModel.getInstance().getTimerView().getNumberOfFlips() < MiniModel.getInstance().getTimerView().getTotalFlips()) {
                    add("Flip timer");
                }
            }
            add("Place marker");
        }});
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 0) {
            return new PickCommandsTuiScreen();
        }

        if (selected == 1) {
            return new PutCommandsTuiScreen();
        }

        StatusEvent status;
        if (selected == 2) {
            status = RotateTile.requester(Client.transceiver, new Object())
                    .request(new RotateTile(MiniModel.getInstance().getUserID(), clientPlayer.getHand().getID()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
            }
            return this;
        }

        if (MiniModel.getInstance().getBoardView().getLevel().equals(LevelView.SECOND)) {
            if (selected == 3) {
                return new DeckCommandsTuiScreen();
            }
            if (MiniModel.getInstance().getTimerView().getNumberOfFlips() < MiniModel.getInstance().getTimerView().getTotalFlips() - 1) {
                if (selected == 4) {
                    status = FlipTimer.requester(Client.transceiver, new Object()).request(new FlipTimer(MiniModel.getInstance().getUserID()));
                    if (status.get().equals("POTA")) {
                        setMessage(((Pota) status).errorMessage());
                    }
                    return this;
                }
                selected -= 2;
            }
            else {
                selected -= 1;
            }
        }

        if (selected == 3) {
            return new ChoosePositionTuiScreen();
        }

        return this;
    }
}
