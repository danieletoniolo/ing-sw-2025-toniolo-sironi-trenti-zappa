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

public class Deck implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));
    private boolean isNewScreen;
    private final TuiScreenView nextScreen;

    private final DeckView myDeck;
    int selected;
    int totalLines = DeckView.getRowsToDraw() + 4 + 2;
    protected String message;
    private final int num;

    public Deck(DeckView deck, int num) {
        this.myDeck = deck;
        this.num = num;
        this.isNewScreen = true;

        nextScreen = new MainBuilding();
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

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

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
        nextScreen.setMessage(message);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Deck;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
