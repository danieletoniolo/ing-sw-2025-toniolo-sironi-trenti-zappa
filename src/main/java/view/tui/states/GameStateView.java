package view.tui.states;

import view.structures.board.BoardView;
import view.structures.deck.DeckView;
import view.structures.player.PlayerDataView;
import view.tui.input.Command;

import java.util.ArrayList;

public class GameStateView implements StateView{
    private BoardView boardView;
    private ArrayList<PlayerDataView> playersView;
    private ArrayList<DeckView> decksView;

    public GameStateView() {

    }

    @Override
    public StateView isValidCommand(Command command) {
        return null; // Placeholder for the next state
    }

    @Override
    public void printTui() {
        System.out.println("Game state view");
    }
}
