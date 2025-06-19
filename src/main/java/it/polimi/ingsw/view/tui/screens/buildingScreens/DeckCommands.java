package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.deck.PickLeaveDeck;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.Deck;
import it.polimi.ingsw.view.tui.screens.TuiScreenView;

import java.util.ArrayList;

public class DeckCommands extends Building {

    public DeckCommands() {
        super(new ArrayList<>(){{
            add("Deck 1");
            add("Deck 2");
            add("Deck 3");
            add("Back");
        }});
    }

    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 3) {
            return new MainBuilding();
        }

        StatusEvent status = PickLeaveDeck.requester(Client.transceiver, new Object())
                .request(new PickLeaveDeck(MiniModel.getInstance().getUserID(), 0, selected));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return new Deck(decksView.getValue0()[selected], selected + 1);
    }
}
