package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
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

    // Tmp variable to store the choice of the fragment
    private int fragmentChoice;
    // Tmp variable to store the choice of the component to destroy
    private List<Pair<Integer, Integer>> componentsToDestroy;

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
        this.fragmentChoice = -1;
        this.componentsToDestroy = null;
        this.internalState = ValidationInternalState.DEFAULT;
    }

    /**
     * Implementation of {@link State#setFragmentChoice(int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (internalState != ValidationInternalState.FRAGMENTED_SHIP) {
            throw new IllegalStateException("No fragment to choose");
        }
        // Check if the fragment choice is valid
        if (fragmentChoice < 0 || fragmentChoice >= fragmentedComponents.size()) {
            throw new IllegalArgumentException("Fragment choice is out of bounds");
        }
        this.fragmentChoice = fragmentChoice;
    }


    @Override
    public void setComponentToDestroy(PlayerData player, List<Pair<Integer, Integer>> componentsToDestroy) throws IllegalStateException {
        if (internalState != ValidationInternalState.DEFAULT) {
            throw new IllegalStateException("Cannot destroy componentsToDestroy in this state, you have to choose a fragment");
        }
        for (Pair<Integer, Integer> component : componentsToDestroy) {
            try {
                player.getSpaceShip().getComponent(component.getValue0(), component.getValue1());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid row" + component.getValue0() + "or column " + component.getValue1() + "for the component to destroy");
            }
        }
        this.componentsToDestroy = componentsToDestroy;
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
        }
    }

    @Override
    public void execute(PlayerData player) {
        ComponentDestroyed destroyableComponentEvent;
        SpaceShip ship = player.getSpaceShip();
        switch (internalState) {
            case DEFAULT:
                if (componentsToDestroy == null) {
                    throw new IllegalStateException("Player has not set the components to destroy");
                }
                // Get the invalid components of the player
                ArrayList<Pair<Integer, Integer>> playerInvalidComponents = invalidComponents.get(player);
                // Destroy the components given by the player
                for (Pair<Integer, Integer> component : componentsToDestroy) {
                    ship.destroyComponent(component.getValue0(), component.getValue1());
                    playerInvalidComponents.remove(component);
                }

                destroyableComponentEvent = new ComponentDestroyed(player.getUsername(), componentsToDestroy);
                eventCallback.trigger(destroyableComponentEvent);

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
                }
                break;
            case FRAGMENTED_SHIP:
                if (fragmentChoice == -1) {
                    throw new IllegalStateException("Player has not set the fragment choice");
                }
                // Destroy all the other fragment of the ship other than the chosen one
                for (List<Pair<Integer, Integer>> chosenFragment : fragmentedComponents) {
                    if (fragmentedComponents.indexOf(chosenFragment) != fragmentChoice) {
                        // Destroy the components in the chosen fragment
                        for (Pair<Integer, Integer> component : chosenFragment) {
                            ship.destroyComponent(component.getValue0(), component.getValue1());
                        }
                    }
                }

                destroyableComponentEvent = new ComponentDestroyed(player.getUsername(), componentsToDestroy);
                eventCallback.trigger(destroyableComponentEvent);

                // Reset the fragmented components and the fragment choice
                fragmentedComponents = null;
                fragmentChoice = -1;
                // Switch the internal state to DEFAULT
                internalState = ValidationInternalState.DEFAULT;
                // Set the player status to PLAYED
                playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
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

