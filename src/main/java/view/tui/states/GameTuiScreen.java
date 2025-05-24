package view.tui.states;

import org.jline.terminal.Terminal;
import view.miniModel.MiniModel;
import view.miniModel.board.BoardView;
import view.miniModel.cards.CardView;
import view.miniModel.components.ComponentView;
import view.miniModel.deck.DeckView;
import view.miniModel.player.PlayerDataView;
import view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public abstract class GameTuiScreen implements TuiScreenView {
    protected ArrayList<String> options = new ArrayList<>();
    private int totalLines;
    protected int selected;
    private String message;

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
                + 1 + clientPlayer.getShip().getRowsToDraw() + 2 + 3;

    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
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
        writer.print("\033[H\033[2J");
        writer.flush();

        for (int i = 0; i < Math.max(boardView.getRowsToDraw(), DeckView.getRowsToDraw()); i++) {
            StringBuilder str = new StringBuilder();

            if (i < boardView.getRowsToDraw()) {
                str.append(boardView.drawLineTui(i));
            } else {
                str.append(" ".repeat(Math.max(0, boardView.getColsToDraw())));
            }

            str.append("                       ");
            if (i < DeckView.getRowsToDraw()) {
                str.append(shuffledDeckView.drawLineTui(i));
            } else {
                str.append(" ".repeat(Math.max(0, shuffledDeckView.getColsToDraw())));
            }

            writer.println(str);
        }
        writer.flush();
        writer.println();

        int playerCont = 0;
        for (int i = 0; i < clientPlayer.getShip().getRowsToDraw(); i++) {
            StringBuilder str = new StringBuilder();
            str.append(clientPlayer.getShip().drawLineTui(i));

            if (i == 0) {
                str.append(" Discard pile: ");
            }
            else if (i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) - 1) {
                str.append(clientPlayer.getShip().getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i >= (clientPlayer.getShip().getRowsToDraw()-1) / 5 * 3 && i < (clientPlayer.getShip().getRowsToDraw()-1) / 5 * 3 + clientPlayer.getRowsToDraw()) {
                str.append("       ");
                str.append(clientPlayer.drawLineTui(playerCont));
                playerCont++;
            }
            writer.println(str);
        }
        writer.flush();
        writer.println();

        writer.println(message == null ? "" : message);
        writer.println();

        PlayerDataView currentPlayer = MiniModel.getInstance().currentPlayer;
        writer.println(currentPlayer.equals(clientPlayer) ? "Your turn" : "Waiting for " + currentPlayer.drawLineTui(0) + "'s turn");
        writer.println();
        writer.println(lineBeforeInput());
        writer.flush();
    }

    protected String lineBeforeInput() {
        return "Commands: ";
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
