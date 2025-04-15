package Model.State;

import Model.Cards.OpenSpace;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.UsableEngine;

import java.util.HashMap;
import java.util.Map;

public class OpenSpaceState extends State implements UsableEngine {
    private Map<PlayerData, Float> stats;

    /**
     * Constructor
     * @param board The board associated with the game
     * @param card card type
     */
    public OpenSpaceState(Board board, OpenSpace card) {
        super(board);
        this.stats = new HashMap<>();
    }

    public Map<PlayerData, Float> getStats() {
        return stats;
    }

    /**
     * Use the cannon
     * @param player PlayerData of the player using the cannon
     * @param strength Strength of the cannon to be used
     */
    public void useEngine(PlayerData player, Float strength) {
        this.stats.merge(player, strength, Float::sum);
    }

    /**
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        for (PlayerData player : super.players) {
            this.useEngine(player, (float) player.getSpaceShip().getSingleEnginesStrength());
            if (player.getSpaceShip().hasBrownAlien()) {
                this.useEngine(player, SpaceShip.getAlienStrength());
            }
        }
    }

    /**
     * Execute: Add steps to player
     * @param player PlayerData of the player to play
     * @throws IllegalStateException Player has not set if adds strength
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        if (stats.get(player) == 0) {
            player.setGaveUp(true);
        }
        else {
            board.addSteps(player, stats.get(player).intValue());
        }
        super.execute(player);
    }
}
