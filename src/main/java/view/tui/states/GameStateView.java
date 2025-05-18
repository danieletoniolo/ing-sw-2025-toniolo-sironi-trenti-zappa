package view.tui.states;

import org.jline.terminal.Terminal;
import view.structures.board.BoardView;
import view.structures.deck.DeckView;
import view.structures.player.PlayerDataView;
import view.tui.input.Command;

import java.util.ArrayList;

public class GameStateView  {
    private BoardView boardView;




    public int getTotalLines() {
        return 0; // Placeholder for the total lines to be displayed
    }

    public StateView readInput(Terminal terminal) {
        return null; // Placeholder for the next state
    }

    public void printTui(Terminal terminal) {
        System.out.println("Game state view");
    }
}
