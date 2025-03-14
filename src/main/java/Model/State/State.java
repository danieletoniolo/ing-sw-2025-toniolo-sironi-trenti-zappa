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
     * Execute at the beginning of the state
     */
    public abstract void entry();

    /**
     * Make the player play in the state
     * @param player PlayerData of the player to play
     */
    public void execute(PlayerData player) {
        for (Pair<PlayerData, Boolean> p : players) {
            if (p.getValue0().equals(player)) {
                p.setAt1(true);
                break;
            }
        }
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
