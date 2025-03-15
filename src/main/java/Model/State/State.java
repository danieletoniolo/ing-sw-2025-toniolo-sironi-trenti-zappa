package Model.State;

import Model.Player.PlayerData;
import org.javatuples.Pair;
import Model.State.PlayerStatus;

import java.util.ArrayList;

public abstract class State {
    protected ArrayList<Pair<PlayerData, PlayerStatus>> players;
    private Boolean played;

    /**
     * Constructor for State
     */
    public State(ArrayList<PlayerData> players) throws NullPointerException {
        if (players == null) {
            throw new NullPointerException("players is null");
        }
        this.players = new ArrayList<>();
        for (PlayerData player : players) {
            this.players.add(new Pair<>(player, PlayerStatus.WAITING));
        }
        this.played = false;
    }


    /**
     * Set the status of the player
     * @param player PlayerData of the player to set the status
     * @throws NullPointerException player == null
     */
    private void setStatusPlayer(PlayerData player, PlayerStatus status) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                p.setAt1(status);
                break;
            }
        }
    }

    /**
     * Get the player who has not played yet (current player to play)
     * @return PlayerData of the current player that is playing
     * @throws IllegalStateException if all players have played
     */
    public PlayerData getCurrentPlayer() throws IllegalStateException{
        for (Pair<PlayerData, PlayerStatus> player : players) {
            if (player.getValue1() == PlayerStatus.WAITING) {
                return player.getValue0();
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
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                p.setAt1(PlayerStatus.PLAYING);
                break;
            }
        }
    }

    /**
     * Execute at the beginning of the state
     */
    public void entry() {};

    /**
     * Make the player play in the state
     * @param player PlayerData of the player to play
     * @throws NullPointerException player == null
     */
    public void execute(PlayerData player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1() == PlayerStatus.PLAYING) {
                    p.setAt1(PlayerStatus.PLAYED);
                } else {
                    p.setAt1(PlayerStatus.WAITING);
                }
            }
        }
    }

    /**
     * Check if all players have played
     * @throws IllegalStateException if not all players have played
     */
    public void exit() throws IllegalStateException {
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() == PlayerStatus.WAITING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }

}
