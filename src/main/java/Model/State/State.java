package Model.State;

import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

public abstract class State {
    protected ArrayList<Pair<PlayerData, Boolean>> players;
    private Boolean played;

    /**
     * Constructor for State
     */
    public State(ArrayList<PlayerData> players) {
        this.players = new ArrayList<>();
        for (PlayerData player : players) {
            this.players.add(new Pair<>(player, false));
        }
        this.played = false;
    }


    /**
     * Set the status of the player
     * @param player PlayerData of the player to set the status
     * @return Boolean of the status of the player
     */
    private void setStatusPlayer(PlayerData player, Boolean status) {
        for (Pair<PlayerData, Boolean> p : players) {
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
        for (Pair<PlayerData, Boolean> player : players) {
            if (!player.getValue1()) {
                return player.getValue0();
            }
        }
        throw new IllegalStateException("All players have played");
    }

    /**
     * Make the player playing in the state
     * @param player PlayerData of the player which is playing
     */
    public void play(PlayerData player) {
        this.setStatusPlayer(player, null);
    }

    /**
     * Execute at the beginning of the state
     */
    public void entry() {};

    /**
     * Make the player play in the state
     * @param player PlayerData of the player to play
     */
    public void execute(PlayerData player) {
        this.setStatusPlayer(player, true);
    }

    /**
     * Check if all players have played
     * @throws IllegalStateException if not all players have played
     */
    public void exit() throws IllegalStateException {
        for (Pair<PlayerData, Boolean> p : players) {
            if (!p.getValue1()) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }

}
