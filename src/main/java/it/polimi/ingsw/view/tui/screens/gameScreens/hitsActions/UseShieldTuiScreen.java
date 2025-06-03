package it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions;

import it.polimi.ingsw.event.game.clientToServer.energyUse.UseShield;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class UseShieldTuiScreen extends GameTuiScreen {

    public UseShieldTuiScreen() {
        super(new ArrayList<>(){{
            spaceShipView.getMapBatteries().values().stream()
                    .filter(battery -> battery.getNumberOfBatteries() != 0)
                    .forEach(battery -> add("Use battery " + "(" + battery.getRow() + " " + battery.getCol() + ")"));
        }});
    }

    @Override
    protected String lineBeforeInput() {
        return "Select a battery to active the shield";
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        int ID = spaceShipView.getMapBatteries().keySet().stream()
                .skip(selected)
                .findFirst()
                .orElse(-1);

        StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(MiniModel.getInstance().getUserID(), ID));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
        }
        return this;
    }
}
