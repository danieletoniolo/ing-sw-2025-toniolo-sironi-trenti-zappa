package view.structures;

import org.javatuples.Pair;
import view.structures.board.BoardView;
import view.structures.components.ComponentView;
import view.structures.deck.DeckView;
import view.structures.lobby.LobbyView;
import view.structures.logIn.LogInView;
import view.structures.player.PlayerDataView;

import java.util.ArrayList;

public class MiniModel {
    private static MiniModel instance;

    public LogInView logInView = new LogInView();
    public ArrayList<LobbyView> lobbyViews = new ArrayList<>();
    public Pair<DeckView[], Boolean[]> deckViews = new Pair<>(new DeckView[3], new Boolean[3]);
    public DeckView shuffledDeckView;
    public BoardView boardView;
    public ArrayList<PlayerDataView> players = new ArrayList<>();
    public ArrayList<ComponentView> components = new ArrayList<>();
    public String nickname;
    public String lobbyID;

    public static MiniModel getInstance() {
        if (instance == null) {
            instance = new MiniModel();
        }
        return instance;
    }
}
