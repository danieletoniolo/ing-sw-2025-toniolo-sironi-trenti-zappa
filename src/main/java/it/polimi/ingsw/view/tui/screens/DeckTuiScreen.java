package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.deck.PickLeaveDeck;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.buildingScreens.MainCommandsTuiScreen;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public class DeckTuiScreen implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));
    private final DeckView myDeck;
    int selected;
    int totalLines = DeckView.getRowsToDraw() + 4 + 2;
    protected String message;
    protected boolean isNewScreen;
    private final int num;

    public DeckTuiScreen(DeckView deck, int num) {
        this.myDeck = deck;
        isNewScreen = true;
        this.num = num;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        StatusEvent status = PickLeaveDeck.requester(Client.transceiver, new Object()).request(new PickLeaveDeck(MiniModel.getInstance().getUserID(), 1, (num - 1)));
        if (status.get().equals("POTA")) {
            setMessage(((Pota) status).errorMessage());
            return this;
        }
        return new MainCommandsTuiScreen();
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        int row = 1;

        TerminalUtils.printLine(writer, "Deck " + num + ":", row++);

        for (int i = 0; i < DeckView.getRowsToDraw(); i++) {
            TerminalUtils.printLine(writer, myDeck.drawLineTui(i), row++);
        }

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, "Commands:", row++);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Deck;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
