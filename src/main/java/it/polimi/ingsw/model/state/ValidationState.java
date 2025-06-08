package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.spaceship.InvalidComponents;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.spaceship.ComponentDestroyed;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationState extends State {
    private ValidationInternalState internalState;
    private final Map<PlayerData, ArrayList<Pair<Integer, Integer>>> invalidComponents;
    private List<List<Pair<Integer, Integer>>> fragmentedComponents;

    /**
     * Enum to represent the internal state of the validation state.
     */
    private enum ValidationInternalState {
        DEFAULT,
        FRAGMENTED_SHIP,
    }

    public ValidationState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.invalidComponents = new HashMap<>();
        this.fragmentedComponents = null;
        this.internalState = ValidationInternalState.DEFAULT;
    }

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state");
    }

    /**
     * Implementation of {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        if (internalState != ValidationInternalState.FRAGMENTED_SHIP) {
            throw new IllegalStateException("No fragment to choose");
        }
        // Check if the fragment choice is valid
        if (fragmentChoice < 0 || fragmentChoice >= fragmentedComponents.size()) {
            throw new IllegalArgumentException("Fragment choice is out of bounds");
        }
        Event event = Handler.destroyFragment(player, fragmentedComponents.get(fragmentChoice));
        eventCallback.trigger(event);
    }


    @Override
    public void setComponentToDestroy(PlayerData player, List<Pair<Integer, Integer>> componentsToDestroy) throws IllegalStateException, IllegalArgumentException {
        if (internalState != ValidationInternalState.DEFAULT) {
            throw new IllegalStateException("Cannot destroy componentsToDestroy in this state, you have to choose a fragment");
        }
        SpaceShip ship = player.getSpaceShip();
        for (Pair<Integer, Integer> component : componentsToDestroy) {
            player.getSpaceShip().getComponent(component.getValue0(), component.getValue1());
        }
        // Destroy the components given by the player
        for (Pair<Integer, Integer> component : componentsToDestroy) {
            ship.destroyComponent(component.getValue0(), component.getValue1());
        }
        Event event = new ComponentDestroyed(player.getUsername(), componentsToDestroy);
        eventCallback.trigger(event);
    }

    @Override
    public void entry() {
        for (PlayerData p : players) {
            ArrayList<Pair<Integer, Integer>> playerInvalidComponents = p.getSpaceShip().getInvalidComponents();
            if (!playerInvalidComponents.isEmpty()) {
                invalidComponents.put(p, playerInvalidComponents);
                // Set the player status to PLAYING to indicate they have invalid components
                playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
            }
            InvalidComponents invalidComponentsEvent = new InvalidComponents(p.getUsername(), playerInvalidComponents);
            eventCallback.trigger(invalidComponentsEvent);
        }
    }

    @Override
    public void execute(PlayerData player) {
        SpaceShip ship = player.getSpaceShip();
        switch (internalState) {
            case DEFAULT:
                // Recalculate the invalid components of the player
                invalidComponents.replace(player, ship.getInvalidComponents());
                ArrayList<Pair<Integer, Integer>> playerInvalidComponents = invalidComponents.get(player);

                if (playerInvalidComponents.isEmpty()) {
                    // Check if the ship is now fragmented
                    fragmentedComponents = ship.getDisconnectedComponents();
                    if (fragmentedComponents.size() > 1) {
                        // Set the internal state to FRAGMENTED_SHIP
                        internalState = ValidationInternalState.FRAGMENTED_SHIP;
                    } else {
                        // Reset the fragment components
                        fragmentedComponents = null;
                        // Set the player status to PLAYED
                        playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                    }
                } else {
                    InvalidComponents invalidComponentsEvent = new InvalidComponents(player.getUsername(), playerInvalidComponents);
                    eventCallback.trigger(invalidComponentsEvent);
                }
                break;
            case FRAGMENTED_SHIP:
                fragmentedComponents = null;
                internalState = ValidationInternalState.DEFAULT;
                playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                break;
        }
        super.nextState(GameState.CREW);
    }
    @Override
    public void exit() {
        for (PlayerData p : players) {
            if (!p.getSpaceShip().getInvalidComponents().isEmpty()) {
                throw new IllegalStateException("The player " + p + "has invalid components");
            }
        }
        super.exit();
    }
}

