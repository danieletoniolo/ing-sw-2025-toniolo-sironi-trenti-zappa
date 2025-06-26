package it.polimi.ingsw.view.tui.screens.gameScreens.hitsActions;

import it.polimi.ingsw.event.game.clientToServer.energyUse.UseShield;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.CardsGame;
import it.polimi.ingsw.view.tui.screens.Lobby;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.gameScreens.NotClientTurnCards;

import java.util.ArrayList;
import java.util.List;

public class UseShieldCards extends CardsGame {

    public UseShieldCards() {
        super(new ArrayList<>(){{
            spaceShipView.getMapBatteries().values().stream()
                    .filter(battery -> battery.getNumberOfBatteries() != 0)
                    .forEach(battery -> add("Use battery " + "(" + battery.getRow() + " " + battery.getCol() + ")"));
            add("Not activate the shield");
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

        int ID = spaceShipView.getMapBatteries().entrySet().stream()
                .filter(battery -> battery.getValue().getNumberOfBatteries() != 0)
                .skip(selected)
                .findFirst()
                .map(entry -> entry.getValue().getID())
                .orElse(-1);

        List<Integer> batteries = new ArrayList<>();
        batteries.add(ID);
        StatusEvent status;
        // Player uses the shield battery
        status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(MiniModel.getInstance().getUserID(), batteries));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }
        // Player is ready for the next hit, so we end the turn
        status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return nextScreen == null ? new NotClientTurnCards() : nextScreen;
    }
}
