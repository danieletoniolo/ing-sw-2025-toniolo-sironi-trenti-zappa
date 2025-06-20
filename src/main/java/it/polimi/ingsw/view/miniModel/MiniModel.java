package it.polimi.ingsw.view.miniModel;

import it.polimi.ingsw.view.miniModel.components.ViewablePileView;
import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.board.BoardView;
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
    private final DeckView shuffledDeckView = new DeckView();
    private BoardView boardView;
    private final ArrayList<PlayerDataView> otherPlayers = new ArrayList<>();
    private final ViewablePileView viewablePileView = new ViewablePileView();
    private int numberHiddenComponents;
    private PlayerDataView clientPlayer;
    private Pair<Integer, Integer> dice;
    private String nickname;
    private String userID;

    private PlayerDataView currentPlayer;
    private LobbyView currentLobby;
    private GamePhases phase;

    public static MiniModel getInstance() {
        if (instance == null) {
            instance = new MiniModel();
        }
        return instance;
    }

    public void setGamePhase(int phase) {
        this.phase = GamePhases.fromValue(phase);
    }

    public GamePhases getGamePhase() {
        return phase;
    }

    public synchronized int getNumberViewableComponents() {
        return numberHiddenComponents;
    }

    public synchronized void setNumberHiddenComponents(int numberHiddenComponents) {
        this.numberHiddenComponents = numberHiddenComponents;
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
        return boardView.getDecksView();
    }

    public synchronized DeckView getShuffledDeckView() {
        return shuffledDeckView;
    }

    public synchronized TimerView getTimerView() {
        return boardView.getTimerView();
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

    public synchronized ViewablePileView getViewablePile() {
        return viewablePileView;
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

    public synchronized void setDice(Pair<Integer, Integer> dice) {
        this.dice = dice;
    }

    public synchronized Pair<Integer, Integer> getDice() {
        return dice;
    }

    public synchronized String getErrorCode() {
        return "POTA";
    }
}
