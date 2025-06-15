package it.polimi.ingsw.view.tui.screens.buildingScreens;

import com.fasterxml.jackson.databind.jsontype.impl.MinimalClassNameIdResolver;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.cheatCode.CheatCode;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.tui.screens.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class CheatShipScreen extends BuildingTuiScreen {

    public CheatShipScreen() {
        super(new ArrayList<>(){{
            if (MiniModel.getInstance().getBoardView().getLevel().equals(LevelView.LEARNING)) {

            }
            else{
                add("Cabins");
                add("Engine and cannons");
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

        if (selected == 2) {
            return new MainCommandsTuiScreen();
        }

        StatusEvent status = CheatCode.requester(Client.transceiver, new Object()).request(new CheatCode(MiniModel.getInstance().getUserID(), selected));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return new MainCommandsTuiScreen();
    }
}
