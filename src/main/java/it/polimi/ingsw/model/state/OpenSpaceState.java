package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenSpaceState represents the state where players move through open space.
 * In this state, players use their engine strength to advance on the board.
 * @author Vittorio Sironi
 */
public class OpenSpaceState extends State {
    /** Map storing the accumulated engine strength for each player in this turn */
    private final Map<PlayerData, Float> stats;
    /** Flag indicating if a player has been forced to give up due to lack of engines */
    private boolean hasPlayerForceGiveUp;

    /**
     * Constructor
     * @param board The board associated with the game
     */
    public OpenSpaceState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.stats = new HashMap<>();
        this.hasPlayerForceGiveUp = false;
    }

    /**
     * Allows a player to use extra strength in the open space state.
     * Only engine strength (type 0) is allowed in this state.
     *
     * @param player The player using extra strength
     * @param type The type of extra strength (0 for engines, 1 for cannons)
     * @param IDs List of component IDs to use for extra strength
     * @param batteriesID List of battery IDs to power the components
     * @throws IllegalStateException If the player is forced to give up or tries to use cannons
     * @throws IllegalArgumentException If an invalid type is provided
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException {
        if (this.hasPlayerForceGiveUp) {
            throw new IllegalStateException("You are forced to give up, you cannot use extra strength");
        }

        switch (type) {
            case 0 -> {
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                this.stats.merge(player, player.getSpaceShip().getEnginesStrength(IDs), Float::sum);
                eventCallback.trigger(event);
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
                initialStrength += ship.getAlienStrength(true);
            }
            this.stats.put(player, initialStrength);
        }
        super.entry();
    }

    /**
     * Execute: Add position to player
     * @param player PlayerData of the player to play
     * @throws IllegalStateException Player has not set if adds strength
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        if (stats.get(player) == 0 && !this.hasPlayerForceGiveUp) {
            ForcingGiveUp forcingGiveUpEvent = new ForcingGiveUp(player.getUsername(), "You have no engines, you have to give up");
            eventCallback.trigger(forcingGiveUpEvent, player.getUUID());
            this.hasPlayerForceGiveUp = true;
        } else {
            if (!this.hasPlayerForceGiveUp){
                board.addSteps(player, stats.get(player).intValue());

                MoveMarker stepEvent = new MoveMarker(player.getUsername(), player.getModuleStep(board.getStepsForALap()));
                eventCallback.trigger(stepEvent);
            }

            this.hasPlayerForceGiveUp = false;

            super.execute(player);

            try {
                CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.getCurrentPlayer().getUsername());
                eventCallback.trigger(currentPlayerEvent);
            }
            catch(Exception e) {
                // Ignore the exception
            }

            super.nextState(GameState.CARDS);
        }
    }

    /**
     * Exit method called when leaving the OpenSpaceState.
     * Performs cleanup operations before transitioning to the next state.
     */
    @Override
    public void exit() {
        super.exit();
    }
}
