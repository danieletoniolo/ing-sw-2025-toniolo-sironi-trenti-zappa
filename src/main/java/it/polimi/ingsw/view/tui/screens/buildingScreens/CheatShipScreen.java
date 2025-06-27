package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.cheatCode.CheatCode;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class CheatShipScreen extends Building {

    public CheatShipScreen() {
        super(new ArrayList<>(){{
            if (MiniModel.getInstance().getBoardView().getLevel().equals(LevelView.LEARNING)) {
                add("Big flight");
                add("Normal ship");
            }
            else{
                add("Cabins");
                add("Engine and cannons");
                add("Invalid cannons and engine");
                add("Fragments");
            }
            add("Back");
        }});
    }

    @Override
    protected String lineBeforeInput() {
        return "You can now cheat or view other players' ships.";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == (MiniModel.getInstance().getBoardView().getLevel() == LevelView.LEARNING ? 2 : 4)) {
            return new MainBuilding();
        }

        // Request the cheat code for the selected option
        StatusEvent status = CheatCode.requester(Client.transceiver, new Object()).request(new CheatCode(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return new MainBuilding();
    }
}
