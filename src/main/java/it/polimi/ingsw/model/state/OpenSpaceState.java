package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.OpenSpace;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSpaceState extends State {
    private final OpenSpace card;
    private final Map<PlayerData, Float> stats;


    /**
     * Constructor
     * @param board The board associated with the game
     * @param card card type
     */
    public OpenSpaceState(Board board, EventCallback callback, OpenSpace card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.stats = new HashMap<>();
        // TODO: We never use the card in this state, so we can remove it
        this.card = card;
    }

    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException {
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
        super.nextState(GameState.CARDS);
    }
}
