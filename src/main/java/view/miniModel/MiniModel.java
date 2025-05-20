package view.miniModel;

import org.javatuples.Pair;
import view.miniModel.board.BoardView;
import view.miniModel.components.ComponentView;
import view.miniModel.deck.DeckView;
import view.miniModel.lobby.LobbyView;
import view.miniModel.logIn.LogInView;
import view.miniModel.player.PlayerDataView;

import java.util.ArrayList;

public class MiniModel {
    private static MiniModel instance;

    public LogInView logInView = new LogInView();
    public ArrayList<LobbyView> lobbyViews = new ArrayList<>();
    /* The decks are stored in a Pair: The first element is the deck views, and the second element is a boolean array.
    If boolean[i] = true the deck[i] is not taken by a player else deck is taken and not viewable in the building state*/
    public Pair<DeckView[], Boolean[]> deckViews = new Pair<>(new DeckView[3], new Boolean[3]);
    public DeckView myDeck;
    public DeckView shuffledDeckView;
    public BoardView boardView;
    public ArrayList<PlayerDataView> players = new ArrayList<>();
    public ArrayList<ComponentView> components = new ArrayList<>();
    public String nickname;
    public String lobbyID;
    public String playerTurn;

    // View Attributes
    public String playerToView;

    public static MiniModel getInstance() {
        if (instance == null) {
            instance = new MiniModel();
        }
        return instance;
    }
}
