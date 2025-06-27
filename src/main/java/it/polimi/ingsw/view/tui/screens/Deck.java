package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.deck.PickLeaveDeck;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.buildingScreens.MainBuilding;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Deck screen in the TUI.
 * Allows the user to view and interact with a deck, including leaving the deck.
 */
public class Deck implements TuiScreenView {
    /**
     * List of available options for the user.
     */
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));

    /**
     * Indicates if this is a new screen instance.
     */
    private boolean isNewScreen;

    /**
     * The next screen to display after this one.
     */
    private final TuiScreenView nextScreen;

    /**
     * The deck view associated with this screen.
     */
    private final DeckView myDeck;

    /**
     * The currently selected option index.
     */
    int selected;

    /**
     * Total number of lines to draw in the TUI.
     */
    int totalLines = DeckView.getRowsToDraw() + 4 + 2;

    /**
     * Message to display to the user.
     */
    protected String message;

    /**
     * The deck number.
     */
    private final int num;

    /**
     * Constructs a Deck screen for the given deck and number.
     *
     * @param deck the deck view to display
     * @param num the deck number
     */
    public Deck(DeckView deck, int num) {
        this.myDeck = deck;
        this.num = num;
        this.isNewScreen = true;

        nextScreen = new MainBuilding();
    }

    /**
     * Reads the user's command input using the provided parser.
     *
     * @param parser the input parser
     */
    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    /**
     * Handles the logic for setting a new screen based on the user's selection.
     *
     * @return the next TuiScreenView to display
     */
    @Override
    public TuiScreenView setNewScreen() {
        if (selected == 0) {
            // Leave the selected deck
            StatusEvent status = PickLeaveDeck.requester(Client.transceiver, new Object()).request(new PickLeaveDeck(MiniModel.getInstance().getUserID(), 1, (num - 1)));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                setMessage(((Pota) status).errorMessage());
                return this;
            }

            return nextScreen;
        }

        return this;
    }

    /**
     * Prints the deck screen to the terminal.
     */
    @Override
    public void printTui() {
        List<String> newLines = new ArrayList<>();

        newLines.add("Deck " + num + ":");

        for (int i = 0; i < DeckView.getRowsToDraw(); i++) {
            newLines.add(myDeck.drawLineTui(i));
        }

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add("Commands:");

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    /**
     * Sets the message to display and propagates it to the next screen.
     *
     * @param message the message to set
     */
    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
        nextScreen.setMessage(message);
    }

    /**
     * Returns the type of this TUI screen.
     *
     * @return the TuiScreens enum value for this screen
     */
    @Override
    public TuiScreens getType() {
        return TuiScreens.Deck;
    }

    /**
     * Sets the next screen to display. (Currently not implemented)
     *
     * @param nextScreen the next TuiScreenView
     */
    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
