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

/**
 * The DeckCommands class represents a screen in the TUI for selecting and interacting with decks.
 * It extends the Building class and provides options for the user to pick a deck or go back.
 */
public class DeckCommands extends Building {

    /**
     * Constructs a DeckCommands screen with predefined options for deck selection and navigation.
     */
    public DeckCommands() {
        super(new ArrayList<>(){{
            add("Deck 1");
            add("Deck 2");
            add("Deck 3");
            add("Back");
        }});
    }

    /**
     * Handles the logic for setting the next screen based on the user's selection.
     * If "Back" is selected, returns to the MainBuilding screen.
     * If a deck is selected, sends a request to pick or leave the deck and handles errors.
     *
     * @return the next TuiScreenView to display, or this screen if an error occurs.
     */
    @Override
    public TuiScreenView setNewScreen() {
        TuiScreenView possibleScreen = super.setNewScreen();
        if (possibleScreen != null) return possibleScreen;

        if (selected == 3) {
            return new MainBuilding();
        }

        // Check if the selected deck is valid
        StatusEvent status = PickLeaveDeck.requester(Client.transceiver, new Object())
                .request(new PickLeaveDeck(MiniModel.getInstance().getUserID(), 0, selected));
        if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }

        return new Deck(decksView.getValue0()[selected], selected + 1);
    }
}
