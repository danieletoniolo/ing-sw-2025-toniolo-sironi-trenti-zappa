package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.game.serverToClient.spaceship.CanProtect;
import it.polimi.ingsw.event.game.serverToClient.spaceship.HitComing;
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

public class MeteorSwarmState extends State {
    private final MeteorSwarm card;
    private final Map<PlayerData, MeteorSwarmInternalState> internalStates;
    private final Map<PlayerData, MutablePair<Component, Integer>> protectionResult;
    private final Map<PlayerData, List<List<Pair<Integer, Integer>>>> fragments;
    private final MutablePair<Integer, Integer> dice;
    private final ArrayList<PlayerData> playersGivenUp;
    private boolean diceRolled = false;
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

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state BuildingState");
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
     * Implementation of the {@link State#setProtect(PlayerData, int)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
        if (!diceRolled) {
            throw new IllegalStateException("Dice not rolled yet");
        }
        Event event = Handler.protectFromHit(player, protectionResult.get(player), batteryID);
        if (event != null) {
            eventCallback.trigger(event);
        }
        event = Handler.checkForFragments(player, fragments.get(player));
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
                // TODO: TO TEST THE GIVE UP
                if (spaceShip.getHumanCrewNumber() == 0 && !playersGivenUp.contains(player)) {
                    this.playersGivenUp.add(player);
                }

                super.execute(player);

                boolean allPlayersPlayed = true;
                for (PlayerData p : players) {
                    if (playersStatus.get(p.getColor()) == PlayerStatus.PLAYING || playersStatus.get(p.getColor()) == PlayerStatus.WAITING) {
                        allPlayersPlayed = false;
                        Logger.getInstance().logError("Player " + p.getUsername() + " has not played yet", true);
                        break;
                    }
                }

                Logger.getInstance().logError("allPlayersPlayed: " + allPlayersPlayed + " meteorsIndex: " + meteorsIndex, true);
                if (allPlayersPlayed) {
                    meteorsIndex++;
                    diceRolled = false;
                    if (meteorsIndex >= card.getMeteors().size()) {
                        Logger.getInstance().logError("No meteorsIndex: " + meteorsIndex, true);
                        if (!playersGivenUp.isEmpty()) {
                            Logger.getInstance().logError("playersGivenUp: " + playersGivenUp, true);
                            for (PlayerData p : playersGivenUp) {
                                internalStates.put(p, MeteorSwarmInternalState.GIVE_UP);

                                ForcingGiveUp forcingGiveUpEvent = new ForcingGiveUp(p.getUsername(), "You are forced to give up, you have no human crew left");
                                eventCallback.trigger(forcingGiveUpEvent, p.getUUID());

                                playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                            }
                        }
                    } else {
                        Logger.getInstance().logError("HitComing: " + meteorsIndex, true);
                        internalStates.put(players.getFirst(), MeteorSwarmInternalState.ROLL_DICE);
                        for (PlayerData p : players) {
                            playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                        }

                        HitComing hitComingEvent = new HitComing(players.getFirst().getUsername());
                        eventCallback.trigger(hitComingEvent);
                    }
                }
                break;
            case GIVE_UP:
                super.execute(player);
                break;
        }

        super.nextState(GameState.CARDS);
    }
}