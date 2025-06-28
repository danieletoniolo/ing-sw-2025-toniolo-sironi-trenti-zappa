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

/**
 * Singleton class representing the mini model of the client-side view.
 * Holds references to all viewable components and game state information.
 */
public class MiniModel {
    /** Singleton instance */
    private static MiniModel instance;

    /// MiniModel structures:
    /** View for login operations */
    private final LogInView logInView = new LogInView();
    /** List of available lobbies */
    private final ArrayList<LobbyView> lobbiesView = new ArrayList<>();
    /** Countdown timer for lobby or game events */
    private CountDown countDown;
    /** Shuffled deck view */
    private final DeckView shuffledDeckView = new DeckView();
    /** Board view representing the current game board */
    private BoardView boardView;
    /** List of other players' data */
    private final ArrayList<PlayerDataView> otherPlayers = new ArrayList<>();
    /** Viewable pile view */
    private final ViewablePileView viewablePileView = new ViewablePileView();
    /** Number of hidden components in the view */
    private int numberHiddenComponents;
    /** Data of the client player */
    private PlayerDataView clientPlayer;
    /** Current dice values */
    private Pair<Integer, Integer> dice;
    /** Nickname of the client player */
    private String nickname;
    /** User ID of the client player */
    private String userID;
    /** Current reward phase */
    private int rewardPhase;

    /** Data of the current player */
    private PlayerDataView currentPlayer;
    /** Current lobby view */
    private LobbyView currentLobby;
    /** Current game phase */
    private GamePhases phase;

    private int combatZonePhase = 0;

    /**
     * Returns the singleton instance of MiniModel.
     * @return the MiniModel instance
     */
    public static MiniModel getInstance() {
        if (instance == null) {
            instance = new MiniModel();
        }
        return instance;
    }

    /**
     * Private constructor to prevent instantiation.
     * @param combatZonePhase the initial combat zone phase
     */
    public void setCombatZonePhase(int combatZonePhase) {
        this.combatZonePhase = combatZonePhase;
    }

    /**
     * Gets the current combat zone phase.
     * @return the combat zone phase
     */
    public int getCombatZonePhase() {
        return combatZonePhase;
    }

    /**
     * Sets the current reward phase.
     * @param rewardPhase the reward phase to set
     */
    public synchronized void setRewardPhase(int rewardPhase) {
        this.rewardPhase = rewardPhase;
    }

    /**
     * Gets the current reward phase.
     * @return the reward phase
     */
    public synchronized int getRewardPhase() {
        return rewardPhase;
    }

    /**
     * Sets the current game phase.
     * @param phase the phase value to set
     */
    public synchronized void setGamePhase(int phase) {
        this.phase = GamePhases.fromValue(phase);
    }

    /**
     * Gets the current game phase.
     * @return the current game phase
     */
    public synchronized GamePhases getGamePhase() {
        return phase;
    }

    /**
     * Gets the number of hidden components in the view.
     * @return the number of hidden components
     */
    public synchronized int getNumberViewableComponents() {
        return numberHiddenComponents;
    }

    /**
     * Sets the number of hidden components in the view.
     * @param numberHiddenComponents the number to set
     */
    public synchronized void setNumberHiddenComponents(int numberHiddenComponents) {
        this.numberHiddenComponents = numberHiddenComponents;
    }

    /**
     * Gets the login view.
     * @return the LogInView instance
     */
    public synchronized LogInView getLogInView() {
        return logInView;
    }

    /**
     * Gets the list of lobby views.
     * @return the list of LobbyView
     */
    public synchronized ArrayList<LobbyView> getLobbiesView() {
        return lobbiesView;
    }

    /**
     * Sets the countdown timer.
     * @param countDown the CountDown to set
     */
    public synchronized void setCountDown(CountDown countDown) {
        this.countDown = countDown;
    }

    /**
     * Gets the countdown timer.
     * @return the CountDown instance
     */
    public synchronized CountDown getCountDown() {
        return countDown;
    }

    /**
     * Gets the deck views from the board.
     * @return a Pair containing arrays of DeckView and Boolean
     */
    public synchronized Pair<DeckView[], Boolean[]> getDeckViews() {
        return boardView.getDecksView();
    }

    /**
     * Gets the shuffled deck view.
     * @return the DeckView instance
     */
    public synchronized DeckView getShuffledDeckView() {
        return shuffledDeckView;
    }

    /**
     * Gets the timer view from the board.
     * @return the TimerView instance
     */
    public synchronized TimerView getTimerView() {
        return boardView.getTimerView();
    }

    /**
     * Sets the board view.
     * @param boardView the BoardView to set
     */
    public synchronized void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }

    /**
     * Gets the board view.
     * @return the BoardView instance
     */
    public synchronized BoardView getBoardView() {
        return boardView;
    }

    /**
     * Gets the list of other players' data.
     * @return the list of PlayerDataView
     */
    public synchronized ArrayList<PlayerDataView> getOtherPlayers() {
        return otherPlayers;
    }

    /**
     * Gets the viewable pile view.
     * @return the ViewablePileView instance
     */
    public synchronized ViewablePileView getViewablePile() {
        return viewablePileView;
    }

    /**
     * Sets the client player data.
     * @param clientPlayer the PlayerDataView to set
     */
    public synchronized void setClientPlayer(PlayerDataView clientPlayer) {
        this.clientPlayer = clientPlayer;
    }

    /**
     * Gets the client player data.
     * @return the PlayerDataView instance
     */
    public synchronized PlayerDataView getClientPlayer() {
        return clientPlayer;
    }

    /**
     * Sets the nickname of the client player.
     * @param nickname the nickname to set
     */
    public synchronized void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets the nickname of the client player.
     * @return the nickname
     */
    public synchronized String getNickname() {
        return nickname;
    }

    /**
     * Sets the user ID of the client player and notifies all waiting threads.
     * @param userID the user ID to set
     */
    public synchronized void setUserID(String userID) {
        this.userID = userID;
        this.notifyAll();
    }

    /**
     * Gets the user ID of the client player.
     * @return the user ID
     */
    public synchronized String getUserID() {
        return userID;
    }

    /**
     * Sets the current player data.
     * @param currentPlayer the PlayerDataView to set
     */
    public synchronized void setCurrentPlayer(PlayerDataView currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Gets the current player data.
     * @return the PlayerDataView instance
     */
    public synchronized PlayerDataView getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the current lobby view.
     * @param currentLobby the LobbyView to set
     */
    public synchronized void setCurrentLobby(LobbyView currentLobby) {
        this.currentLobby = currentLobby;
    }

    /**
     * Gets the current lobby view.
     * @return the LobbyView instance
     */
    public synchronized LobbyView getCurrentLobby() {
        return currentLobby;
    }

    /**
     * Sets the current dice values.
     * @param dice the Pair of dice values to set
     */
    public synchronized void setDice(Pair<Integer, Integer> dice) {
        this.dice = dice;
    }

    /**
     * Gets the current dice values.
     * @return the Pair of dice values
     */
    public synchronized Pair<Integer, Integer> getDice() {
        return dice;
    }

    /**
     * Gets the error code.
     * @return the error code string
     */
    public synchronized String getErrorCode() {
        return "POTA";
    }
}
