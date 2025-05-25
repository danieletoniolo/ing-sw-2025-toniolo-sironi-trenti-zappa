package it.polimi.ingsw.model.state;

import it.polimi.ingsw.model.cards.OpenSpace;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.EnginesUsed;
import it.polimi.ingsw.event.game.serverToClient.MoveMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSpaceState extends State {
    private final Map<PlayerData, Float> stats;

    /**
     * Constructor
     * @param board The board associated with the game
     * @param card card type
     */
    public OpenSpaceState(Board board, EventCallback callback, OpenSpace card) {
        super(board, callback);
        this.stats = new HashMap<>();
    }

    public Map<PlayerData, Float> getStats() {
        return stats;
    }

    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException {
        switch (type) {
            case 0 -> {
                // Use the energy to power the engines
                SpaceShip ship = player.getSpaceShip();
                for (Integer batteryID : batteriesID) {
                    ship.useEnergy(batteryID);
                }

                // Update the engine strength stats
                this.stats.merge(player, (float) IDs.size() * 2, Float::sum);

                EnginesUsed useEnginesEvent = new EnginesUsed(player.getUsername(), IDs, (ArrayList<Integer>) batteriesID);
                eventCallback.trigger(useEnginesEvent);
            }
            case 1 -> throw new IllegalStateException("Cannot use double cannons in this state");
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        }
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
     * Execute: Add position to player
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

            MoveMarker stepEvent = new MoveMarker(player.getUsername(), player.getStep());
            eventCallback.trigger(stepEvent);
        }
        super.execute(player);
    }
}
