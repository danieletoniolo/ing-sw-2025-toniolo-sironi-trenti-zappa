package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPenalty;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.game.serverToClient.spaceship.CanProtect;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.MeteorSwarm;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import it.polimi.ingsw.model.state.utils.MutablePair;
import it.polimi.ingsw.utils.Logger;
import org.javatuples.Pair;

import java.util.*;

/**
 * State class that handles the MeteorSwarm card event in the game.
 * This state manages the meteor attack sequence including dice rolling,
 * protection decisions, and penalty application for all players.
 * @author Vittorio Sironi
 */
public class MeteorSwarmState extends State {
    /** The MeteorSwarm card that triggered this state */
    private final MeteorSwarm card;

    /** Maps each player to their current internal state in the meteor sequence */
    private final Map<PlayerData, MeteorSwarmInternalState> internalStates;

    /** Maps each player to their protection result (component and battery cost) */
    private final Map<PlayerData, MutablePair<Component, Integer>> protectionResult;

    /** Maps each player to their list of fragment groups that can be destroyed */
    private final Map<PlayerData, List<List<Pair<Integer, Integer>>>> fragments;

    /** The dice values rolled for the meteor attack */
    private final MutablePair<Integer, Integer> dice;

    /** List of players who have given up due to having no human crew */
    private final ArrayList<PlayerData> playersGivenUp;

    /** Flag indicating whether the dice have been rolled for the current meteor */
    private boolean diceRolled = false;

    /** Index of the current meteor being processed from the card's meteor list */
    private int meteorsIndex;

    /**
     * Internal state of the MeteorSwarmState.
     */
    enum MeteorSwarmInternalState {
        ROLL_DICE,
        PENALTY,
        GIVE_UP
    }

    /**
     * Constructor
     * @param board The board associated with the game
     * @param card card type
     */
    public MeteorSwarmState(Board board, EventCallback callback, MeteorSwarm card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        this.protectionResult = new HashMap<>();
        this.meteorsIndex = 0;
        this.dice = new MutablePair<>(-1, -1);
        this.playersGivenUp = new ArrayList<>();

        this.fragments = new HashMap<>();
        for (PlayerData p : players) {
            this.fragments.put(p, new ArrayList<>());
        }

        this.internalStates = new HashMap<>();
        for (PlayerData p : players) {
            this.internalStates.put(p, MeteorSwarmInternalState.PENALTY);
        }
        this.internalStates.put(players.getFirst(), MeteorSwarmInternalState.ROLL_DICE);
    }

    /**
     * Throws a SynchronousStateException as this is a synchronous state where the concept of a "current player" doesn't apply in the traditional sense.
     * All players participate simultaneously in the meteor swarm event.
     *
     * @return Never returns a value, always throws an exception
     * @throws SynchronousStateException Always thrown to indicate this operation
     *         is not supported in synchronous states
     */
    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state MeteorSwarmState");
    }

    /**
     * Implementation of {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        if (fragments.isEmpty()) {
            throw new IllegalStateException("No fragments to choose from");
        }
        for (int i = 0; i < fragments.get(player).size(); i++) {
            if (i != fragmentChoice) {
                List<Event> events = Handler.destroyFragment(player, fragments.get(player).get(i));
                for (Event e : events) {
                    eventCallback.trigger(e);
                }
            }
        }
        fragments.get(player).clear();
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, List)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, List<Integer> batteryID) throws IllegalStateException, IllegalArgumentException {
        if (!diceRolled) {
            throw new IllegalStateException("Dice not rolled yet");
        }
        if (batteryID.size() != 1) {
            throw new IllegalArgumentException("Battery ID must be a list of size 1");
        }
        if (batteryID.getFirst() != -1 && protectionResult.get(player).getSecond() == -1) {
            throw new IllegalArgumentException("You cannot set a shield if because you cannot protect from the hit");
        }

        List<Event> events = Handler.protectFromHit(player, protectionResult.get(player), batteryID.getFirst());
        if (events != null) {
            for (Event event : events ) {
                eventCallback.trigger(event);
            }
        }
        Event event = Handler.checkForFragments(player, fragments.get(player));
        eventCallback.trigger(event);
    }

    /**
     * Implementation of the {@link State#rollDice(PlayerData)} to roll the dice and set the value in the fight handler.
     */
    @Override
    public void rollDice(PlayerData player) throws IllegalStateException {
        if (diceRolled) {
            throw new IllegalStateException("Dice already rolled for this hit");
        }
        if (players.indexOf(player) != 0) {
            throw new IllegalStateException("Player cannot roll dice. Only the leader can");
        }
        Event event = Handler.rollDice(player, dice);
        eventCallback.trigger(event);
        diceRolled = true;
    }

    /**
     * Entry point of the MeteorSwarmState that initializes the state by setting the first player
     * as the current player and triggering the CurrentPlayer event to notify clients.
     * This method is called when the state is first entered.
     */
    @Override
    public void entry() {
        CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.players.getFirst().getUsername());
        eventCallback.trigger(currentPlayerEvent);
    }

    /**
     * Execute: Check if player can protect, destroy components if necessary, choose which components to keep if necessary
     * @param player PlayerData of the player to play
     * @throws IndexOutOfBoundsException hitIndex out of bounds, toKeepComponents is out of bounds
     * @throws IllegalStateException Dice not set
     */
    @Override
    public void execute(PlayerData player) throws IndexOutOfBoundsException, IllegalStateException {
        if (!diceRolled) {
            throw new IllegalStateException("Dice not rolled yet");
        }

        SpaceShip spaceShip = player.getSpaceShip();
        switch (internalStates.get(player)) {
            case ROLL_DICE:
                int diceValue = dice.getFirst() + dice.getSecond() - 1;

                for (PlayerData p : players) {
                    protectionResult.put(p, new MutablePair<>(p.getSpaceShip().canProtect(diceValue, card.getMeteors().get(meteorsIndex))));
                    Component component = protectionResult.get(p).getFirst();
                    CanProtect canProtectEvent = new CanProtect(p.getUsername(), new Pair<>(component != null ? component.getID() : null, protectionResult.get(p).getSecond()));
                    eventCallback.trigger(canProtectEvent, p.getUUID());
                }
                internalStates.put(player, MeteorSwarmInternalState.PENALTY);
                break;
            case PENALTY:
                if (fragments.get(player).size() > 1) {
                    break;
                }
                fragments.get(player).clear();
                if (spaceShip.getHumanCrewNumber() == 0 && !playersGivenUp.contains(player)) {
                    this.playersGivenUp.add(player);
                }

                super.execute(player);

                boolean allPlayersPlayed = true;
                try {
                    allPlayersPlayed();
                } catch (IllegalStateException e) {
                    allPlayersPlayed = false;
                }

                if (allPlayersPlayed) {
                    meteorsIndex++;
                    diceRolled = false;
                    if (meteorsIndex >= card.getMeteors().size()) {
                        if (!playersGivenUp.isEmpty()) {
                            for (PlayerData p : playersGivenUp) {
                                internalStates.put(p, MeteorSwarmInternalState.GIVE_UP);

                                ForcingGiveUp forcingGiveUpEvent = new ForcingGiveUp(p.getUsername(), "You are forced to give up, you have no human crew left");
                                eventCallback.trigger(forcingGiveUpEvent, p.getUUID());

                                playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                            }
                        }
                    } else {
                        internalStates.put(players.getFirst(), MeteorSwarmInternalState.ROLL_DICE);
                        for (PlayerData p : players) {
                            playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                        }

                        ForcingPenalty forcingPenalty = new ForcingPenalty(players.getFirst().getUsername(), PenaltyType.HIT_PENALTY.getValue());
                        eventCallback.trigger(forcingPenalty);
                    }
                }
                break;
            case GIVE_UP:
                super.execute(player);
                break;
        }

        super.nextState(GameState.CARDS);
    }

    /**
     * Cleanup method called when exiting the MeteorSwarmState.
     * Performs any necessary cleanup operations before transitioning to another state.
     */
    @Override
    public void exit() {
        super.exit();
    }
}