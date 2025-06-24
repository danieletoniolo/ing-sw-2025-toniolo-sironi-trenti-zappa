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

public class OpenSpaceState extends State {
    private final Map<PlayerData, Float> stats;
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
                initialStrength += SpaceShip.getAlienStrength();
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
}
