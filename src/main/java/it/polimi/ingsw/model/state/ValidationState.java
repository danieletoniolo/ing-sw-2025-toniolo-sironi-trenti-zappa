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

public class ValidationState extends State {
    private final Map<PlayerData, ArrayList<Pair<Integer, Integer>>> invalidComponents;
    private final Map<PlayerData, List<List<Pair<Integer, Integer>>>> fragmentedComponents;
    private final Map<PlayerData, Boolean> playersNeedToPlaceMarker;
    private final ArrayList<PlayerData> playersWithMarkerOnBoard;

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

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state ValidationState");
    }

    @Override
    public void placeMarker(PlayerData player, int position) throws IllegalStateException {
        if (super.board.getBoardLevel() != Level.LEARNING) {
            throw new IllegalStateException("Cannot place marker if you are not in a Learning Game");
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


    @Override
    public void setComponentToDestroy(PlayerData player, List<Pair<Integer, Integer>> componentsToDestroy) throws IllegalStateException, IllegalArgumentException {
        if (componentsToDestroy.isEmpty()) {
            return;
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

