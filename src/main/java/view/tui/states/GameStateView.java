package view.tui.states;

import org.jline.terminal.Terminal;
import view.structures.board.BoardView;
import view.structures.deck.DeckView;
import view.structures.player.PlayerDataView;
import view.tui.input.Command;

import java.util.ArrayList;

public class GameStateView {
    private BoardView boardView;
    private ArrayList<PlayerDataView> playersView;
    private ArrayList<DeckView> decksView;

    public StateView readInput(Terminal terminal) {
        return null; // Placeholder for the next state
    }

    public void printTui(Terminal terminal) {
        System.out.println("Game state view");
    }
}
