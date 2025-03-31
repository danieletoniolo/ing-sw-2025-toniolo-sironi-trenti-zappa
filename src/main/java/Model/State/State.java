package Model.State;

import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

enum PlayerStatus {
    WAITING,
    PLAYING,
    PLAYED,
    SKIPPED
}

public abstract class State {
    protected ArrayList<Pair<PlayerData, PlayerStatus>> players;
    protected Boolean played;

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
     * Get the position of the player
     * @param player PlayerData of the player to get the position
     * @return position of the player
     * @throws IllegalArgumentException if the player is not found
     */
    protected int getPlayerPosition(PlayerData player) throws IllegalArgumentException {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getValue0().equals(player)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Player not found");
    }

    /**
     * Check if all players have played
     * @return Boolean value if all players have played
     */
    protected boolean haveAllPlayersPlayed() {
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() != PlayerStatus.PLAYED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Set the status of the player
     * @param player PlayerData of the player to set the status
     * @throws NullPointerException player == null
     */
    protected void setStatusPlayer(PlayerData player, PlayerStatus status) throws NullPointerException {
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
     * Set the status of all players
     * @param status PlayerStatus to set to all players
     */
    protected void setStatusPlayers(PlayerStatus status) {
        for (Pair<PlayerData, PlayerStatus> p : players) {
            p.setAt1(status);
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
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1() == PlayerStatus.PLAYING) {
                    p.setAt1(PlayerStatus.PLAYED);
                } else {
                    p.setAt1(PlayerStatus.SKIPPED);
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
        this.played = true;
    }

}
