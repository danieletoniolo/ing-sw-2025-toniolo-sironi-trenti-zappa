package it.polimi.ingsw.view.tui.screens.crewScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.screens.ModifyCrew;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class MainCrew extends ModifyCrew {

    public MainCrew() {
        super(new ArrayList<>(){{
            if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.SECOND) {
                add("Add humans");
                add("Add brown alien");
                add("Add purple alien");
                add("Remove crew from cabin");
            }
            add("Done");
        }});
        if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.SECOND) {
            setMessage("Modify your crew members");
        }
        if (MiniModel.getInstance().getBoardView().getLevel() == LevelView.LEARNING) {
            setMessage("Human crew members have boarded your spaceship!");
        }
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == options.size() - 2 - MiniModel.getInstance().getOtherPlayers().size()) {
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            if (nextScreen == null) {
                return new WaitingCrew();
            }
            return nextScreen;
        }

        return new AddCrew(selected);
    }
}
