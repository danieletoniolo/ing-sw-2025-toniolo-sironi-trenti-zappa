package it.polimi.ingsw.view.tui.states;

import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class GameTuiScreen implements TuiScreenView {
    protected ArrayList<String> options = new ArrayList<>();
    private int totalLines;
    protected int selected;
    private int row;
    protected String message;
    protected boolean isNewScreen;

    private BoardView boardView = MiniModel.getInstance().boardView;
    private PlayerDataView clientPlayer = MiniModel.getInstance().clientPlayer;
    private DeckView shuffledDeckView = MiniModel.getInstance().shuffledDeckView;
    private CardView currentCard = MiniModel.getInstance().shuffledDeckView.getDeck().peek();


    public GameTuiScreen(List<String> otherOptions) {
        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);

        for (PlayerDataView p : MiniModel.getInstance().otherPlayers) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }

        totalLines = Math.max(boardView.getRowsToDraw(), DeckView.getRowsToDraw())
                + 1 + clientPlayer.getShip().getRowsToDraw() + 2 + 2 + 2;

        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if (selected < options.size() && selected >= options.size() - MiniModel.getInstance().otherPlayers.size()) {
            int i = selected - (options.size() - MiniModel.getInstance().otherPlayers.size());

            return new PlayerTuiScreen(MiniModel.getInstance().otherPlayers.get(i), this);
        }

        return null;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        row = 1;

        for (int i = 0; i < Math.max(boardView.getRowsToDraw(), DeckView.getRowsToDraw()); i++) {
            StringBuilder line = new StringBuilder();

            if (i < boardView.getRowsToDraw()) {
                line.append(boardView.drawLineTui(i));
            } else {
                line.append(" ".repeat(Math.max(0, boardView.getColsToDraw())));
            }

            line.append("                       ");
            if (i < DeckView.getRowsToDraw()) {
                line.append(shuffledDeckView.drawLineTui(i));
            } else {
                line.append(" ".repeat(Math.max(0, shuffledDeckView.getColsToDraw())));
            }

            TerminalUtils.printLine(writer, line.toString(), row++);
        }

        int playerCount = 0;
        for (int i = 0; i < clientPlayer.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(clientPlayer.getShip().drawLineTui(i));

            if (i == 0) {
                line.append(" Discard pile: ");
            }
            else if (i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) - 1) {
                line.append(clientPlayer.getShip().getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i > ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 4 + 1) - 1 && i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 4 + clientPlayer.getRowsToDraw())) {
                line.append("   ").append(clientPlayer.drawLineTui(playerCount));
                if (playerCount == 0) {
                    line.append("    ");
                }
                playerCount++;
            }
            TerminalUtils.printLine(writer, line.toString(), row++);
        }

        TerminalUtils.printLine(writer, "", row++);
        PlayerDataView currentPlayer = MiniModel.getInstance().currentPlayer;
        String turn = currentPlayer.equals(clientPlayer) ? "Your turn" : "Waiting for " + currentPlayer.drawLineTui(0) + "'s turn";
        TerminalUtils.printLine(writer, turn, row++);

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, lineBeforeInput(), row++);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }

    }

    protected String lineBeforeInput() {
        return "Commands: ";
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Game;
    }
}
