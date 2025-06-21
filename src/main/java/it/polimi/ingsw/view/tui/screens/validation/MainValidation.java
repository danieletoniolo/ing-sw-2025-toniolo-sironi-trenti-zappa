package it.polimi.ingsw.view.tui.screens.validation;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.DestroyComponents;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;
import it.polimi.ingsw.view.tui.screens.Validation;

import java.util.ArrayList;

public class MainValidation extends Validation {
    private boolean hasWrong;

    public MainValidation(){
        super(new ArrayList<>(){{
            boolean hasWrong = false;
            for (ComponentView[] row : MiniModel.getInstance().getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView tile : row) {
                    if (tile != null && tile.getIsWrong()) {
                        hasWrong = true;
                        break;
                    }
                }
            }
            if (hasWrong) {
                add("Destroy a component");
                add("Cancel");
            }
            add("Confirm spaceship");
        }});

        hasWrong = false;
        for (ComponentView[] row : MiniModel.getInstance().getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView tile : row) {
                if (tile != null && tile.getIsWrong()) {
                    hasWrong = true;
                    break;
                }
            }
        }
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (!hasWrong) {
            selected += 2; // Adjust for the "Destroy a component" and "Cancel" options
        }

        switch (selected) {
            case 0:
                return new RowAndColValidation(this);
            case 1:
                destroyStatics();
                return new MainValidation();
            case 2:
                // Send the destroy request to the server
                StatusEvent status = DestroyComponents.requester(Client.transceiver, new Object()).request(
                        new DestroyComponents(MiniModel.getInstance().getUserID(), destroyTiles));
                destroyTiles.clear();
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    destroyStatics();
                    return new MainValidation();
                }
                destroyStatics();

                // End the turn after destroying components
                status = EndTurn.requester(Client.transceiver, new Object()).request(
                        new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    setMessage(((Pota) status).errorMessage());
                    return new MainValidation();
                }

                return nextState;
        }

        return this;
    }
}
