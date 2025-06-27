package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPlaceMarker;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.player.RemoveMarker;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
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

/**
 * ValidationState represents a synchronous game state where players' spaceships are validated
 * for invalid components and fragmentation. This state handles the removal of invalid components
 * and manages player markers on the board for learning games.
 * @author Vittorio Sironi
 */
public class ValidationState extends State {
    /** Map storing invalid components for each player */
    private final Map<PlayerData, ArrayList<Pair<Integer, Integer>>> invalidComponents;
    /** Map storing fragmented component groups for each player */
    private final Map<PlayerData, List<List<Pair<Integer, Integer>>>> fragmentedComponents;
    /** Map tracking which players need to place a marker on the board */
    private final Map<PlayerData, Boolean> playersNeedToPlaceMarker;
    /** List of players who currently have a marker on the board */
    private final ArrayList<PlayerData> playersWithMarkerOnBoard;

    /**
     * Constructs a new ValidationState with the specified game components.
     * Initializes all internal data structures and sets up player tracking.
     *
     * @param board the game board
     * @param callback the event callback for triggering events
     * @param transitionHandler the handler for state transitions
     */
    public ValidationState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.invalidComponents = new HashMap<>();
        this.fragmentedComponents = new HashMap<>();
        this.playersNeedToPlaceMarker = new HashMap<>();
        this.playersWithMarkerOnBoard = new ArrayList<>(players);

        for (PlayerData player : players) {
            this.playersNeedToPlaceMarker.put(player, false);
        }
    }

    /**
     * Returns the current player in the game.
     * This method is not supported in ValidationState as it represents a synchronous state
     * where all players act simultaneously rather than in turns.
     *
     * @return the current player
     * @throws SynchronousStateException always thrown since this operation is not valid in synchronous states
     */
    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state ValidationState");
    }

    /**
     * Places a player's marker on the board at the specified position.
     * This method is only available in Learning Games and handles marker placement
     * and movement events. If position is -1, the marker is removed from the board.
     *
     * @param player the player whose marker is being placed
     * @param position the board position where to place the marker, or -1 to remove it
     * @throws IllegalStateException if not in a Learning Game or if player already has a marker on board
     */
    @Override
    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        if (super.board.getBoardLevel() != Level.LEARNING) {
            throw new IllegalStateException("Cannot place marker if you are not in a Learning Game");
        }
        if (playersWithMarkerOnBoard.contains(player)) {
            throw new IllegalStateException("You are already on the board");
        }

        board.setPlayer(player, position);

        if (position != -1) {
            MoveMarker moveMarkerEvent = new MoveMarker(player.getUsername(), player.getModuleStep(board.getStepsForALap()));
            eventCallback.trigger(moveMarkerEvent);
        } else {
            RemoveMarker removeMarkerEvent = new RemoveMarker(player.getUsername());
            eventCallback.trigger(removeMarkerEvent);

            for (PlayerData p : playersWithMarkerOnBoard) {
                MoveMarker moveMarkerEvent = new MoveMarker(p.getUsername(), p.getModuleStep(board.getStepsForALap()));
                eventCallback.trigger(moveMarkerEvent);
            }
        }
    }

    /**
     * Implementation of {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        if (fragmentedComponents.get(player).size() <= 1) {
            throw new IllegalStateException("The player " + player.getUsername() + " has no fragments to destroy");
        }

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


    /**
     * Sets the components to be destroyed for a specific player.
     * This method handles the destruction of invalid components from a player's spaceship,
     * updating engine and cannon strengths if affected components are destroyed.
     *
     * @param player the player whose components are being destroyed
     * @param componentsToDestroy list of component coordinates (row, column) to be destroyed
     * @throws IllegalStateException if the operation cannot be completed due to game state
     * @throws IllegalArgumentException if the provided components are invalid
     */
    @Override
    public void setComponentToDestroy(PlayerData player, List<Pair<Integer, Integer>> componentsToDestroy) throws IllegalStateException, IllegalArgumentException {
        if (componentsToDestroy.isEmpty()) {
            return;
        }
        if (invalidComponents.get(player).isEmpty()) {
            throw new IllegalStateException("The player " + player.getUsername() + " has no components to destroy");
        }

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
            SetEngineStrength engineStrength = new SetEngineStrength(player.getUsername(), ship.getDefaultEnginesStrength(), ship.getMaxEnginesStrength());
            eventCallback.trigger(engineStrength);
        }
        if (isCannon) {
            SetCannonStrength cannonStrength = new SetCannonStrength(player.getUsername(), ship.getDefaultCannonsStrength(), ship.getMaxCannonsStrength());
            eventCallback.trigger(cannonStrength);
        }
    }

    /**
     * Entry point for the ValidationState. This method is called when the state is first entered.
     * It initializes the validation process by:
     * - Identifying invalid components for each player's spaceship
     * - Setting all players to PLAYING status
     * - Triggering InvalidComponents events for each player
     * - For Learning Games: removing players with invalid components from the board
     */
    @Override
    public void entry() {
        ArrayList<Pair<Integer, Integer>> playerInvalidComponents;
        for (PlayerData p : players) {
            playerInvalidComponents = p.getSpaceShip().getInvalidComponents();
            invalidComponents.put(p, playerInvalidComponents);
            playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);

            InvalidComponents invalidComponentsEvent = new InvalidComponents(p.getUsername(), playerInvalidComponents);
            eventCallback.trigger(invalidComponentsEvent);

            if (!playerInvalidComponents.isEmpty() && board.getBoardLevel() == Level.LEARNING) {
                playersWithMarkerOnBoard.remove(p);
                placeMarker(p, -1);
            }
        }
    }

    /**
     * Executes the validation logic for a specific player.
     * This method handles different scenarios based on the player's current state:
     * - If the player needs to place a marker (Learning Games), forces marker placement
     * - Otherwise, recalculates invalid components and checks for ship fragmentation
     * - Manages player status and triggers appropriate events
     * - Transitions to CREW state when validation is complete
     *
     * @param player the player for whom to execute the validation logic
     */
    @Override
    public void execute(PlayerData player) {
        SpaceShip ship = player.getSpaceShip();

        if (playersNeedToPlaceMarker.get(player) && board.getBoardLevel() == Level.LEARNING) {
            super.execute(player);
            playersNeedToPlaceMarker.put(player, false);
            playersWithMarkerOnBoard.add(player);
        } else {
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
                    if (board.getBoardLevel() == Level.LEARNING && !playersWithMarkerOnBoard.contains(player)) {
                        playersNeedToPlaceMarker.put(player, true);

                        ForcingPlaceMarker forcingPlaceMarkerEvent = new ForcingPlaceMarker(player.getUsername());
                        eventCallback.trigger(forcingPlaceMarkerEvent);
                    } else {
                        super.execute(player);
                    }
                }

                eventCallback.trigger(event);
            }
        }

        super.nextState(GameState.CREW);
    }

    /**
     * Exit point for the ValidationState. This method is called when leaving the state.
     * Performs final validation to ensure all players have valid spaceships before transitioning
     * to the next state. Throws an exception if any player still has invalid components.
     *
     * @throws IllegalStateException if any player still has invalid components when exiting the state
     */
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

