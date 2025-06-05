package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.tui.screens.buildingScreens.MainCommandsTuiScreen;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {
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
}
