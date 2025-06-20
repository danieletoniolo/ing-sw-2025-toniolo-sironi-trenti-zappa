package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.DestroyComponents;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.ValidationTuiScreen;

import java.util.List;

public class MainValidation extends ValidationTuiScreen {

    public MainValidation(){
        super(List.of("Destroy a component", "Cancel", "Done"));

    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        switch (selected) {
            case 0:
                return new RowAndColValidation(this);
            case 1:
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                destroyTiles.clear();
                return this;
            case 2:
                StatusEvent status = DestroyComponents.requester(Client.transceiver, new Object()).request(
                        new DestroyComponents(MiniModel.getInstance().getUserID(), destroyTiles));
                destroyTiles.clear();
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                    return this;
                }
                status = EndTurn.requester(Client.transceiver, new Object()).request(
                        new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals("POTA")) {
                    setMessage(((Pota) status).errorMessage());
                    spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
                    return this;
                }
                setMessage(null);
                spaceShipView = MiniModel.getInstance().getClientPlayer().getShip();

                return nextState;
        }

        return this;
    }
}
