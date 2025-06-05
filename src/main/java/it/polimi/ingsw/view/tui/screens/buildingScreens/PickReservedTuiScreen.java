package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromReserve;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.tui.screens.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class PickReservedTuiScreen extends BuildingTuiScreen {

    public PickReservedTuiScreen() {
        super(new ArrayList<>(){{
            for (int i = 0; i < MiniModel.getInstance().getClientPlayer().getShip().getDiscardReservedPile().getReserved().size(); i++) {
                add((i + 1) + "");
            }
        }});
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int ID = MiniModel.getInstance().getClientPlayer().getShip().getDiscardReservedPile().getReserved().stream()
                .skip(selected)
                .map(ComponentView::getID)
                .findFirst()
                .orElse(-1);

        if (ID != -1) {
            StatusEvent status = PickTileFromReserve.requester(Client.transceiver, new Object()).request(new PickTileFromReserve(MiniModel.getInstance().getUserID(), ID));
            if (status.get().equals("POTA")) {
                TuiScreenView newScreen = new MainCommandsTuiScreen();
                newScreen.setMessage(((Pota) status).errorMessage());
                return newScreen;
            }
        }

        return new MainCommandsTuiScreen();
    }
}
