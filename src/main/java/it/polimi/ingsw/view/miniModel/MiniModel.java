package it.polimi.ingsw.view.miniModel;

import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.countDown.CountDown;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.logIn.LogInView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;

import java.util.ArrayList;

public class MiniModel {
    private static MiniModel instance;

    /// MiniModel structures:
    public LogInView logInView = new LogInView();
    public ArrayList<LobbyView> lobbiesView = new ArrayList<>();
    public CountDown countDown;
    /** The decks are stored in a Pair: The first element is the deck views, and the second element is a boolean array.
    If boolean[i] == true the deck[i] is not taken by a player, else deck is taken and not viewable in the building screen*/
    public Pair<DeckView[], Boolean[]> deckViews = new Pair<>(new DeckView[3], new Boolean[3]);
    public DeckView shuffledDeckView;
    public TimerView timerView;
    public BoardView boardView;
    public ArrayList<PlayerDataView> otherPlayers = new ArrayList<>();
    public ArrayList<ComponentView> viewableComponents = new ArrayList<>();
    public PlayerDataView clientPlayer;
    public String nickname;
    public String userID;

    public PlayerDataView currentPlayer;
    public LobbyView currentLobby;

    public static MiniModel getInstance() {
        if (instance == null) {
            instance = new MiniModel();
        }
        return instance;
    }
}
