package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.GiveUp;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public abstract class GameTuiScreen implements TuiScreenView {
    protected ArrayList<String> options = new ArrayList<>();
    private final int totalLines;
    protected int selected;
    protected static String message;
    protected static SpaceShipView spaceShipView;

    protected BoardView boardView = MiniModel.getInstance().getBoardView();
    protected PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();
    protected DeckView shuffledDeckView = MiniModel.getInstance().getShuffledDeckView();

    public GameTuiScreen(List<String> otherOptions) {
        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Give up");
        options.add("Close program");

        totalLines = Math.max(boardView.getRowsToDraw(), DeckView.getRowsToDraw())
                + 1 + clientPlayer.getShip().getRowsToDraw() + 2 + 3 + 2;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 2) && (selected >= options.size() - 2 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 2);

            return new PlayerTuiScreen(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 2) {
            StatusEvent status = GiveUp.requester(Client.transceiver, new Object()).request(new GiveUp(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage(((Pota) status).errorMessage());
            }
            return this;
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        return null;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        int row = 1;
        if (spaceShipView == null) {
            spaceShipView = clientPlayer.getShip();
        }

        for (int i = 0; i < Math.max(boardView.getRowsToDraw(), DeckView.getRowsToDraw()); i++) {
            StringBuilder line = new StringBuilder();

            if (i < boardView.getRowsToDraw()) {
                line.append(boardView.drawLineTui(i));
            } else {
                line.append(" ".repeat(Math.max(0, boardView.getColsToDraw())));
            }

            line.append("              ");
            if (i < DeckView.getRowsToDraw()) {
                line.append(shuffledDeckView.drawLineTui(i));
            } else {
                line.append(" ".repeat(Math.max(0, shuffledDeckView.getColsToDraw())));
            }

            TerminalUtils.printLine(writer, line.toString(), row++);
        }
        TerminalUtils.printLine(writer, "", row++);

        int playerCount = 0;
        for (int i = 0; i < spaceShipView.getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(spaceShipView.drawLineTui(i));

            if (i == 0) {
                line.append(" Discard pile: ").append(clientPlayer.getShip().getDiscardReservedPile().getReserved().size());
            }
            else if (i <= ((spaceShipView.getRowsToDraw() - 2) / 5 + 1) - 1) {
                line.append(" ").append(spaceShipView.getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i > ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + 1) - 1 && i <= ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + clientPlayer.getRowsToDraw())) {
                line.append("   ").append(clientPlayer.drawLineTui(playerCount));
                if (playerCount == 0) {
                    line.append("    ");
                }
                playerCount++;
            }
            TerminalUtils.printLine(writer, line.toString(), row++);
        }

        TerminalUtils.printLine(writer, "", row++);
        PlayerDataView currentPlayer = MiniModel.getInstance().getCurrentPlayer();
        String turn = currentPlayer.equals(clientPlayer) ? "Your turn" : "Waiting for " + currentPlayer.drawLineTui(0) + "'s turn";
        TerminalUtils.printLine(writer, turn, row++);

        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, lineBeforeInput(), row);

        TerminalUtils.clearLastLines(totalLines + options.size(), terminal);
    }

    protected String lineBeforeInput() {
        return "Commands: ";
    }

    @Override
    public synchronized void setMessage(String message) {
        GameTuiScreen.message = message;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Game;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
