package Model.State;

import Model.Cards.OpenSpace;
import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.UsableEngine;
import controller.event.game.MoveMarker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSpaceState extends State implements UsableEngine {
    private final Map<PlayerData, Float> stats;

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
    public void useEngine(PlayerData player, Float strength, List<Integer> batteriesID) {
        // Use the energy to power the engines
        SpaceShip ship = player.getSpaceShip();
        for (Integer batteryID : batteriesID) {
            ship.useEnergy(batteryID);
        }

        // Update the engine strength stats
        this.stats.merge(player, strength, Float::sum);
    }

    /**
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        for (PlayerData player : super.players) {
            SpaceShip ship = player.getSpaceShip();
            float initialStrength = ship.getSingleEnginesStrength();
            if (player.getSpaceShip().hasBrownAlien()) {
                initialStrength += SpaceShip.getAlienStrength();
            }
            this.stats.put(player, initialStrength);
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
            this.players = super.board.getInGamePlayers();
        } else {
            board.addSteps(player, stats.get(player).intValue());

            // TODO: EVENT STEPS
            MoveMarker stepEvent = new MoveMarker(player.getColor(), player.getStep());
        }
        super.execute(player);
    }
}
