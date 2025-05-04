package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum PlayerStatus {
    WAITING,
    PLAYING,
    PLAYED,
    SKIPPED
}

public abstract class State {
    protected ArrayList<PlayerData> players;
    protected Map<PlayerColor, PlayerStatus> playersStatus;
    protected Board board;
    protected Boolean played;

    /**
     * Constructor for State
     * @param board Board associated with the game
     * @throws NullPointerException if board is null
     */
    public State(Board board) throws NullPointerException {
        if (board == null) {
            throw new NullPointerException("board is null");
        }
        this.board = board;
        this.players = board.getInGamePlayers();
        this.playersStatus = new HashMap<>();
        for (PlayerData player : players) {
            this.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        }
        this.played = false;
    }

    // TODO: If the method is used only in pirates state, remove it from here
    /**
     * Check if all players have played
     * @return Boolean value if all players have played
     */
    protected boolean haveAllPlayersPlayed() {
        for (PlayerData p : players) {
            if (playersStatus.get(p.getColor()) != PlayerStatus.PLAYED && playersStatus.get(p.getColor()) != PlayerStatus.SKIPPED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Set the status of all players
     * @param status PlayerStatus to set to all players
     */
    protected void setStatusPlayers(PlayerStatus status) {
        for (PlayerData p : players) {
            playersStatus.put(p.getColor(), status);
        }
    }

    /**
     * Get the player who has not played yet (current player to play)
     * @return PlayerData of the current player that is playing
     * @throws IllegalStateException if all players have played
     */
    public PlayerData getCurrentPlayer() throws IllegalStateException{
        for (PlayerData player : players) {
            if (playersStatus.get(player.getColor()) == PlayerStatus.WAITING) {
                return player;
            }
        }
        throw new IllegalStateException("All players have played");
    }

    /**
     * Make the player playing in the state
     * @param player PlayerData of the player which is playing
     * @throws NullPointerException player == null
     */
    public void play(PlayerData player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        playersStatus.replace(player.getColor(), PlayerStatus.PLAYING);
    }

    /**
     * Execute at the beginning of the state
     */
    public void entry() {}

    /**
     * Make the player play in the state
     * @param player PlayerData of the player to play
     * @throws NullPointerException player == null
     */
    public void execute(PlayerData player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }

        PlayerStatus playerStatus = playersStatus.get(player.getColor());
        if (playerStatus == PlayerStatus.PLAYING) {
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
        } else {
            playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
        }
    }

    /**
     * Check if all players have played
     * @throws IllegalStateException if not all players have played
     */
    public void exit() throws IllegalStateException {
        for (PlayerData p : players) {
            if (playersStatus.get(p.getColor()) == PlayerStatus.WAITING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        this.played = true;
        board.refreshInGamePlayers();
    }
}