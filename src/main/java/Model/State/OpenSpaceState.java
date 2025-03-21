package Model.State;

import Model.Cards.OpenSpace;
import Model.Player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenSpaceState extends State {
    private OpenSpace card;
    private Map<PlayerData, Integer> stats;
    private int state;

    /**
     * Constructor
     * @param players players list
     * @param card card type
     */
    public OpenSpaceState(ArrayList<PlayerData> players, OpenSpace card) {
        super(players);
        this.card = card;
        stats = new HashMap<>();
        for (PlayerData player : players) {
            stats.put(player, player.getSpaceShip().getSingleEnginesStrength());
        }
        state = 0;
    }

    /**
     * Add power engine to player
     * @param player current player
     * @param strength (number of double engine turned on) * 2
     * @throws IndexOutOfBoundsException Required strength > max strength player
     * @throws NullPointerException player == null
     * @throws IllegalStateException Player has already set if adds strength
     */
    public void addStrength(PlayerData player, int strength) throws IndexOutOfBoundsException, NullPointerException, IllegalStateException {
        if (state == 1) {
            throw new IllegalStateException("Player has already set if adds strength");
        }
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (player.getSpaceShip().getDoubleEnginesStrength() < strength) {
            throw new IndexOutOfBoundsException("Required strength > max strength player");
        }
        stats.put(player, stats.get(player) + strength);
        state = 1;
    }

    /**
     * Execute: Add steps to player
     * @param player PlayerData of the player to play
     * @throws IllegalStateException Player has not set if adds strength
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        if (state == 0){
            throw new IllegalStateException("Player has not set if adds strength");
        }
        if (stats.get(player) == 0) {
            player.setGaveUp(true);
        }
        else {
            player.addSteps(stats.get(player));
        }
        super.execute(player);
        state = 0;
    }
}
