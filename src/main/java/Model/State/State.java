package Model.State;

import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

public abstract class State {
    private ArrayList<Pair<PlayerData, Boolean>> players;

    /**
     * Constructor for State
     */
    public State() {
        players = new ArrayList<>();
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
     * Initialize the players in the state with the given order and set their turn to false (not played yet)
     * @param players ArrayList of PlayerData to be added to the state in the order they are in the board
     */
    public void entry(ArrayList<PlayerData> players) {
        for (PlayerData player : players) {
            this.players.add(new Pair<>(player, false));
        }
    }

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
    public void exit(PlayerData player) {
        for (Pair<PlayerData, Boolean> p : players) {
            if (!p.getValue1()) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }

}
