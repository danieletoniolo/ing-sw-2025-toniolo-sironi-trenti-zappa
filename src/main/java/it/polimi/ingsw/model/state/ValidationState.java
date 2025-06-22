package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.ComponentType;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationState extends State {
    private final Map<PlayerData, ArrayList<Pair<Integer, Integer>>> invalidComponents;
    private final Map<PlayerData, List<List<Pair<Integer, Integer>>>> fragmentedComponents;

    public ValidationState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.invalidComponents = new HashMap<>();
        this.fragmentedComponents = new HashMap<>();
    }

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state ValidationState");
    }

    /**
     * Implementation of {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        // Check if the fragment choice is valid
        if (fragmentChoice < 0 || fragmentChoice >= fragmentedComponents.get(player).size()) {
            throw new IllegalArgumentException("Fragment choice is out of bounds");
        }

        for (int i = 0; i < fragmentedComponents.get(player).size(); i++) {
            if (i != fragmentChoice) {
                List<Event> events = Handler.destroyFragment(player, fragmentedComponents.get(player).get(i));
                for (Event e : events) {
                    eventCallback.trigger(e);
                }
            }
        }
        fragmentedComponents.put(player, null);
    }


    @Override
    public void setComponentToDestroy(PlayerData player, List<Pair<Integer, Integer>> componentsToDestroy) throws IllegalStateException, IllegalArgumentException {
        SpaceShip ship = player.getSpaceShip();
        boolean isCannon = false, isEngine = false;

        for (Pair<Integer, Integer> component : componentsToDestroy) {
            Component tempComponent = player.getSpaceShip().getComponent(component.getValue0(), component.getValue1());

            if (tempComponent.getComponentType() == ComponentType.SINGLE_CANNON || tempComponent.getComponentType() == ComponentType.DOUBLE_CANNON) {
                isCannon = true;
            } else if (tempComponent.getComponentType() == ComponentType.SINGLE_ENGINE || tempComponent.getComponentType() == ComponentType.DOUBLE_ENGINE) {
                isEngine = true;
            }
        }
        // Destroy the components given by the player
        for (Pair<Integer, Integer> component : componentsToDestroy) {
            ship.destroyComponent(component.getValue0(), component.getValue1());
        }

        Event event = new ComponentDestroyed(player.getUsername(), componentsToDestroy);
        eventCallback.trigger(event);

        if (isEngine) {
            SetEngineStrength engineStrength = new SetEngineStrength(player.getUsername(), ship.getSingleEnginesStrength(), ship.getSingleEnginesStrength() + ship.getDoubleEnginesStrength());
            eventCallback.trigger(engineStrength);
        } else if (isCannon) {
            SetCannonStrength cannonStrength = new SetCannonStrength(player.getUsername(), ship.getSingleCannonsStrength(), ship.getSingleCannonsStrength() + ship.getDoubleCannonsStrength());
            eventCallback.trigger(cannonStrength);
        }
    }

    @Override
    public void entry() {
        for (PlayerData p : players) {
            ArrayList<Pair<Integer, Integer>> playerInvalidComponents = p.getSpaceShip().getInvalidComponents();
            invalidComponents.put(p, playerInvalidComponents);
            // Set the player status to PLAYING to indicate they have invalid components
            playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
            InvalidComponents invalidComponentsEvent = new InvalidComponents(p.getUsername(), playerInvalidComponents);
            eventCallback.trigger(invalidComponentsEvent);
        }
    }

    @Override
    public void execute(PlayerData player) {
        SpaceShip ship = player.getSpaceShip();

        // Recalculate the invalid components of the player
        invalidComponents.put(player, ship.getInvalidComponents());
        ArrayList<Pair<Integer, Integer>> playerInvalidComponents = invalidComponents.get(player);

        InvalidComponents invalidComponentsEvent = new InvalidComponents(player.getUsername(), playerInvalidComponents);
        eventCallback.trigger(invalidComponentsEvent);

        if (playerInvalidComponents.isEmpty()) {
            // Check if the ship is now fragmented
            List<List<Pair<Integer, Integer>>> fragments = new ArrayList<>();
            Event event = Handler.checkForFragments(player, fragments);
            fragmentedComponents.put(player, fragments);
            if (fragmentedComponents.get(player).size() <= 1) {
                playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
            }

            eventCallback.trigger(event);
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

