package Model.State;

import Model.Cards.OpenSpace;
import Model.Player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenSpaceState extends State {
    private OpenSpace card;
    private Map<PlayerData, Integer> stats;

    /**
     *
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
    }

    /**
     *
     * @param player current player
     * @param strength (number of double engine turned on) * 2
     * @throws IndexOutOfBoundsException Required strength > max strength player
     * @throws NullPointerException player == null
     */
    public void addStrength(PlayerData player, int strength) throws IndexOutOfBoundsException, NullPointerException {
        if (player == null) {
            throw new NullPointerException("Player is null");
        }
        if (player.getSpaceShip().getDoubleEnginesStrength() < strength) {
            throw new IndexOutOfBoundsException("Required strength > max strength player");
        }
        stats.put(player, stats.get(player) + strength);
    }

    /**
     * Execute: Add steps to player
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        if (stats.get(player) == 0) {
            player.setGaveUp(true);
        }
        else {
            player.addSteps(stats.get(player));
        }
        super.execute(player);
    }
}
