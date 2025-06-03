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
    private final LogInView logInView = new LogInView();
    private final ArrayList<LobbyView> lobbiesView = new ArrayList<>();
    private CountDown countDown;
    /** The decks are stored in a Pair: The first element is the deck views, and the second element is a boolean array.
    If boolean[i] == true the deck[i] is not taken by a player, else deck is taken and not viewable in the building screen*/
    private final Pair<DeckView[], Boolean[]> deckViews = new Pair<>(new DeckView[3], new Boolean[3]);
    private final DeckView shuffledDeckView = new DeckView();
    private TimerView timerView;
    private BoardView boardView;
    private final ArrayList<PlayerDataView> otherPlayers = new ArrayList<>();
    private final ArrayList<ComponentView> viewableComponents = new ArrayList<>();
    private PlayerDataView clientPlayer;
    private String nickname;
    private String userID;

    private PlayerDataView currentPlayer;
    private LobbyView currentLobby;

    public static MiniModel getInstance() {
        if (instance == null) {
            instance = new MiniModel();
        }
        return instance;
    }

    public synchronized LogInView getLogInView() {
        return logInView;
    }

    public synchronized ArrayList<LobbyView> getLobbiesView() {
        return lobbiesView;
    }

    public synchronized void setCountDown(CountDown countDown) {
        this.countDown = countDown;
    }

    public synchronized CountDown getCountDown() {
        return countDown;
    }

    public synchronized Pair<DeckView[], Boolean[]> getDeckViews() {
        return deckViews;
    }

    public synchronized DeckView getShuffledDeckView() {
        return shuffledDeckView;
    }

    public synchronized void setTimerView(TimerView timerView) {
        this.timerView = timerView;
    }

    public synchronized TimerView getTimerView() {
        return timerView;
    }

    public synchronized void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }

    public synchronized BoardView getBoardView() {
        return boardView;
    }

    public synchronized ArrayList<PlayerDataView> getOtherPlayers() {
        return otherPlayers;
    }

    public synchronized ArrayList<ComponentView> getViewableComponents() {
        return viewableComponents;
    }

    public synchronized void setClientPlayer(PlayerDataView clientPlayer) {
        this.clientPlayer = clientPlayer;
    }

    public synchronized PlayerDataView getClientPlayer() {
        return clientPlayer;
    }

    public synchronized void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public synchronized String getNickname() {
        return nickname;
    }

    public synchronized void setUserID(String userID) {
        this.userID = userID;
        this.notifyAll();
    }

    public synchronized String getUserID() {
        return userID;
    }

    public synchronized void setCurrentPlayer(PlayerDataView currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public synchronized PlayerDataView getCurrentPlayer() {
        return currentPlayer;
    }

    public synchronized void setCurrentLobby(LobbyView currentLobby) {
        this.currentLobby = currentLobby;
    }

    public synchronized LobbyView getCurrentLobby() {
        return currentLobby;
    }
}
