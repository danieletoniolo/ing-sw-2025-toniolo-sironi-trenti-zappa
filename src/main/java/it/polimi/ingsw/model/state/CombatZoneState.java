package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPenalty;
import it.polimi.ingsw.event.game.serverToClient.player.CombatZonePhase;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.spaceship.CanProtect;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.CombatZone;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.model.state.utils.MutablePair;
import org.javatuples.Pair;

import java.util.*;

/**
 * Represents a state in the game where players engage in combat within a combat zone.
 * This state manages the combat phases, player statistics, and internal state transitions
 * during combat encounters.
 * @author Vittorio Sironi
 */
public class CombatZoneState extends State {
    /** The current internal state of the combat zone phase */
    private CombatZoneInternalState internalState;
    /** The combat zone card that defines the rules and parameters for this combat */
    private final CombatZone card;
    /** Map storing each player's accumulated engine strength statistics */
    private final Map<PlayerData, Float> enginesStats;
    /** Map storing each player's accumulated cannon strength statistics */
    private final Map<PlayerData, Float> cannonsStats;
    /** Reference to the player with the minimum engine strength */
    private PlayerData minPlayerEngines;
    /** Reference to the player with the minimum cannon strength */
    private PlayerData minPlayerCannons;
    /** Reference to the player with the minimum crew count */
    private PlayerData minPlayerCrew;
    /** List of fragments generated during combat hits, where each fragment is represented as a list of coordinate pairs */
    private final List<List<Pair<Integer, Integer>>> fragments;
    /** Result of protection attempt, containing the component that can protect and the required battery index */
    private MutablePair<Component, Integer> protectionResult;
    /** List of players who have been forced to give up during combat due to lack of human crew */
    private final ArrayList<PlayerData> playersGivenUp;
    /** Pair of dice values used for protection rolls during combat */
    private final MutablePair<Integer, Integer> dice;
    /** Current index tracking which hit/fire is being processed from the combat zone card */
    private int hitIndex;
    /** Current amount of penalty loss that still needs to be applied to the player */
    private int currentPenaltyLoss;
    /** Flag indicating if a player was forced to give up specifically due to crew penalty */
    private boolean hasPlayerForceGiveUpForCrewPenalty;

    /**
     * Enum to represent the internal state of the combat zone state.
     */
    enum CombatZoneInternalState {
        CREW,
        ENGINES,
        CANNONS,
        CREW_PENALTY,
        GOODS_PENALTY,
        BATTERIES_PENALTY,
        HIT_PENALTY,
        CAN_PROTECT,
        GIVE_UP
    }

    /**
     * Constructs a new CombatZoneState with the specified parameters.
     * Initializes the internal state based on the combat zone card level and sets up
     * all necessary data structures for managing combat phases and player statistics.
     *
     * @param board The game board containing player positions and game state
     * @param callback The event callback handler for triggering game events
     * @param card The combat zone card that defines the rules and parameters for this combat
     * @param transitionHandler The handler responsible for managing state transitions
     */
    public CombatZoneState(Board board, EventCallback callback, CombatZone card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        if (card.getCardLevel() == 2) {
            this.internalState = CombatZoneInternalState.CANNONS;
        } else {
            this.internalState = CombatZoneInternalState.CREW;
        }
        this.minPlayerCannons = null;
        this.minPlayerEngines = null;
        this.minPlayerCrew = null;
        this.currentPenaltyLoss = card.getLost();
        this.fragments = new ArrayList<>();
        this.protectionResult = new MutablePair<>(null, -1);
        this.hitIndex = 0;
        this.dice = new MutablePair<>(-1, -1);
        this.enginesStats = new HashMap<>();
        this.cannonsStats = new HashMap<>();
        this.playersGivenUp = new ArrayList<>();
        this.hasPlayerForceGiveUpForCrewPenalty = false;
    }

    /**
     * Executes the flight days penalty for a player by moving them backwards on the board.
     * This method retrieves the flight days penalty from the combat zone card and applies
     * it as negative steps to the player's position, then triggers a MoveMarker event
     * to notify clients of the position change.
     *
     * @param player The player to apply the flight days penalty to
     */
    private void executeSubStateFlightDays(PlayerData player) {
        int flightDays = card.getFlightDays();
        board.addSteps(player, -flightDays);

        MoveMarker stepsEvent = new MoveMarker(player.getUsername(),  player.getModuleStep(board.getStepsForALap()));
        eventCallback.trigger(stepsEvent);
    }

    /**
     * Sends a combat zone phase event to notify clients of the current combat phase.
     *
     * @param phase The current phase number of the combat zone (0, 1, or 2)
     */
    private void sendCombatZonePhase (int phase) {
        CombatZonePhase combatZonePhase = new CombatZonePhase(phase);
        eventCallback.trigger(combatZonePhase);
    }

    /**
     * Implementation of the {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        if (internalState != CombatZoneInternalState.HIT_PENALTY) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        if (fragments.isEmpty()) {
            throw new IllegalArgumentException("No fragments available to choose from");
        }
        for (int i = 0; i < fragments.size(); i++) {
            if (i != fragmentChoice) {
                List<Event> events = Handler.destroyFragment(player, fragments.get(i));
                for (Event e : events) {
                    eventCallback.trigger(e);
                }
            }
        }
        fragments.clear();
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, List)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, List<Integer> batteryID) throws IllegalStateException, IllegalArgumentException {
        if (internalState != CombatZoneInternalState.HIT_PENALTY) {
            throw new IllegalStateException("Battery ID not allowed in this state");
        }
        if (batteryID.size() != 1) {
            throw new IllegalArgumentException("Battery ID must be a single value");
        }
        if (batteryID.getFirst() != -1 && protectionResult.getSecond() == -1) {
            throw new IllegalArgumentException("You cannot set a shield if because you cannot protect from the hit");
        }

        List<Event> events = Handler.protectFromHit(player, protectionResult, batteryID.getFirst());
        if (events != null) {
            for (Event event : events) {
                eventCallback.trigger(event);
            }
        }
        Event event = Handler.checkForFragments(player, fragments);
        eventCallback.trigger(event);
    }

    /**
     * Implementation of the {@link State#rollDice(PlayerData)} to roll the dice and set the value in the fight handler.
     */
    @Override
    public void rollDice(PlayerData player) throws IllegalStateException {
        if (internalState != CombatZoneInternalState.CAN_PROTECT) {
            throw new IllegalStateException("setDice not allowed in this state");
        }
        Event event = Handler.rollDice(player, dice);
        eventCallback.trigger(event);
    }

    /**
     * Implementation of the {@link State#setPenaltyLoss(PlayerData, int, List)} to set crew members, batteries or goods to leave
     * @throws IllegalArgumentException if the type is not 0, 1 or 2.
     */
    @Override
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> penaltyLoss) throws IllegalStateException {
        switch (type) {
            case 0 -> {
                if (internalState != CombatZoneInternalState.GOODS_PENALTY) {
                    throw new IllegalStateException("There is no penalty to serve.");
                }
                Event event = Handler.loseGoods(player, penaltyLoss, currentPenaltyLoss);
                eventCallback.trigger(event);
                currentPenaltyLoss -= penaltyLoss.size();
            }
            case 1 -> {
                if (internalState != CombatZoneInternalState.BATTERIES_PENALTY) {
                    throw new IllegalStateException("There is no penalty to serve.");
                }
                Event event = Handler.loseBatteries(player, penaltyLoss, currentPenaltyLoss);
                eventCallback.trigger(event);
                currentPenaltyLoss = card.getLost();
            }
            case 2 -> {
                if (internalState != CombatZoneInternalState.CREW_PENALTY) {
                    throw new IllegalStateException("There is no penalty to serve.");
                }
                List<Event> events = Handler.loseCrew(player, penaltyLoss, currentPenaltyLoss);
                for (Event event : events) {
                    eventCallback.trigger(event);
                }
                currentPenaltyLoss -= penaltyLoss.size();
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, List, List)} to use double engines
     * or double cannons based on the {@link CombatZoneInternalState} of the state.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        Float statPlayer;

        switch (type) {
            case 0 -> {
                if (internalState != CombatZoneInternalState.ENGINES) {
                    throw new IllegalStateException("useEngine not allowed in this state");
                }
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                enginesStats.merge(player, player.getSpaceShip().getEnginesStrength(IDs), Float::sum);
                eventCallback.trigger(event);

                statPlayer = enginesStats.get(player);
                if (statPlayer < enginesStats.get(minPlayerEngines)) {
                    minPlayerEngines = player;
                }
            }
            case 1 -> {
                if (internalState != CombatZoneInternalState.CANNONS) {
                    throw new IllegalStateException("useCannon not allowed in this state");
                }
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                cannonsStats.merge(player, player.getSpaceShip().getCannonsStrength(IDs), Float::sum);
                eventCallback.trigger(event);

                statPlayer = cannonsStats.get(player);
                if (statPlayer < cannonsStats.get(minPlayerCannons)) {
                    minPlayerCannons = player;
                }
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }
    }

    /**
     * Entry: Add the default stats to the player
     */
    @Override
    public void entry() {
        if (super.players.size() == 1) {
            super.played = true;
            super.nextState(GameState.CARDS);
            return;
        }

        sendCombatZonePhase(0);
        SpaceShip ship;
        for (PlayerData player : super.players) {
            ship = player.getSpaceShip();
            if (minPlayerCrew == null || minPlayerCrew.getSpaceShip().getCrewNumber() > ship.getCrewNumber()) {
                minPlayerCrew = player;
            }

            Handler.initializeEngineStrengths(player, enginesStats);
            if (minPlayerEngines == null || minPlayerEngines.getSpaceShip().getSingleEnginesStrength() > ship.getSingleEnginesStrength()) {
                minPlayerEngines = player;
            }

            Handler.initializeCannonStrengths(player, cannonsStats);
            if (minPlayerCannons == null || minPlayerCannons.getSpaceShip().getSingleCannonsStrength() > ship.getSingleCannonsStrength()) {
                minPlayerCannons = player;
            }
        }
        if (card.getCardLevel() == 1) {
            for (PlayerData player : players) {
                playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
            }
            playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);

            CurrentPlayer currentPlayerEvent = new CurrentPlayer(minPlayerCrew.getUsername());
            eventCallback.trigger(currentPlayerEvent);
        } else {
            super.entry();
        }
    }

    /**
     * Execute one state each of teh cards
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        boolean sendCurrentPlayer = false;
        SpaceShip spaceShip = player.getSpaceShip();

        switch (internalState) {
            case CREW:
                if (card.getCardLevel() == 2) {
                    internalState = CombatZoneInternalState.CAN_PROTECT;

                    ForcingPenalty forcingPenalty = new ForcingPenalty(minPlayerCrew.getUsername(), PenaltyType.HIT_PENALTY.getValue());
                    eventCallback.trigger(forcingPenalty);
                } else {
                    executeSubStateFlightDays(minPlayerCrew);
                    for (PlayerData p : players) {
                        playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
                    }
                    internalState = CombatZoneInternalState.ENGINES;

                    sendCombatZonePhase(1);
                    sendCurrentPlayer = true;
                }
                break;
            case ENGINES:
                super.execute(player);
                if (players.indexOf(player) == players.size() - 1) {
                    playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
                    if (card.getCardLevel() == 2) {
                        internalState = CombatZoneInternalState.GOODS_PENALTY;

                        ForcingPenalty forcingPenalty = new ForcingPenalty(minPlayerEngines.getUsername(), PenaltyType.GOODS_PENALTY.getValue());
                        eventCallback.trigger(forcingPenalty);
                    } else {
                        internalState = CombatZoneInternalState.CREW_PENALTY;

                        ForcingPenalty forcingPenalty = new ForcingPenalty(minPlayerEngines.getUsername(), PenaltyType.CREW_PENALTY.getValue());
                        eventCallback.trigger(forcingPenalty);
                    }
                } else {
                    sendCurrentPlayer = true;
                }
                break;
            case CANNONS:
                super.execute(player);
                if (players.indexOf(player) == players.size() - 1) {
                    if (card.getCardLevel() == 2) {
                        executeSubStateFlightDays(minPlayerCannons);
                        for (PlayerData p : players) {
                            playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
                        }
                        internalState = CombatZoneInternalState.ENGINES;

                        Handler.initializeCannonStrengths(player, cannonsStats);
                        sendCombatZonePhase(1);
                        sendCurrentPlayer = true;
                    } else {
                        playersStatus.replace(minPlayerCannons.getColor(), PlayerStatus.PLAYING);
                        internalState = CombatZoneInternalState.CAN_PROTECT;

                        ForcingPenalty forcingPenalty = new ForcingPenalty(minPlayerCannons.getUsername(), PenaltyType.HIT_PENALTY.getValue());
                        eventCallback.trigger(forcingPenalty);
                    }
                } else {
                    sendCurrentPlayer = true;
                }
                break;
            case CAN_PROTECT:
                int diceValue = dice.getFirst() + dice.getSecond() - 1;
                protectionResult = new MutablePair<>(player.getSpaceShip().canProtect(diceValue, card.getFires().get(hitIndex)));
                Component component = protectionResult.getFirst();
                CanProtect canProtectEvent = new CanProtect(player.getUsername(), new Pair<>(component != null ? component.getID() : null, protectionResult.getSecond()));
                eventCallback.trigger(canProtectEvent);

                internalState = CombatZoneInternalState.HIT_PENALTY;
                break;
            case GOODS_PENALTY:
                if (currentPenaltyLoss > 0) {
                    internalState = CombatZoneInternalState.BATTERIES_PENALTY;

                    ForcingPenalty forcingPenalty = new ForcingPenalty(minPlayerEngines.getUsername(), PenaltyType.BATTERIES_PENALTY.getValue());
                    eventCallback.trigger(forcingPenalty);
                } else {
                    internalState = CombatZoneInternalState.CREW;

                    sendCombatZonePhase(2);
                    super.execute(player);
                    sendCurrentPlayer = true;

                    Handler.initializeEngineStrengths(player, enginesStats);
                    playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
                }
                break;
            case BATTERIES_PENALTY:
                internalState = CombatZoneInternalState.CREW;

                sendCombatZonePhase(2);
                super.execute(player);
                sendCurrentPlayer = true;

                Handler.initializeEngineStrengths(player, enginesStats);
                playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
                break;
            case CREW_PENALTY:
                if (spaceShip.getHumanCrewNumber() == 0) {
                    ForcingGiveUp lostEvent = new ForcingGiveUp(player.getUsername(), "You are forced to give up, you have no human crew left");
                    eventCallback.trigger(lostEvent, player.getUUID());
                    internalState = CombatZoneInternalState.GIVE_UP;
                    this.hasPlayerForceGiveUpForCrewPenalty = true;
                } else {
                    internalState = CombatZoneInternalState.CANNONS;
                    for (PlayerData p : players) {
                        playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
                    }

                    Handler.initializeEngineStrengths(player, enginesStats);
                    sendCombatZonePhase(2);
                    sendCurrentPlayer = true;
                }
                break;
            case HIT_PENALTY:
                if (fragments.size() > 1) {
                    break;
                }
                fragments.clear();

                if (spaceShip.getHumanCrewNumber() == 0 && !playersGivenUp.contains(player)) {
                    this.playersGivenUp.add(player);
                }

                hitIndex++;
                Handler.initializeEngineStrengths(player, enginesStats);
                Handler.initializeCannonStrengths(player, cannonsStats);
                if (hitIndex >= card.getFires().size()) {
                    if (!playersGivenUp.isEmpty()) {
                        internalState = CombatZoneInternalState.GIVE_UP;
                        ForcingGiveUp forcingGiveUpEvent = new ForcingGiveUp(playersGivenUp.getFirst().getUsername(), "You are forced to give up, you have no human crew left");
                        eventCallback.trigger(forcingGiveUpEvent);

                        for (PlayerData p : playersGivenUp) {
                            playersStatus.replace(p.getColor(), PlayerStatus.WAITING);
                        }
                    } else {
                        super.execute(player);
                    }
                } else {
                    ForcingPenalty forcingPenalty = new ForcingPenalty(player.getUsername(), PenaltyType.HIT_PENALTY.getValue());
                    eventCallback.trigger(forcingPenalty);

                    internalState = CombatZoneInternalState.CAN_PROTECT;
                }
                break;
            case GIVE_UP:
                super.execute(player);

                if (!hasPlayerForceGiveUpForCrewPenalty) {
                    try {
                        ForcingGiveUp forcingGiveUpEvent = new ForcingGiveUp(getCurrentPlayer().getUsername(), "You are forced to give up, you have no human crew left");
                        eventCallback.trigger(forcingGiveUpEvent);
                    } catch (IllegalStateException e) {
                        // Ignore the exception, it means there are no more players left to give up
                    }
                } else {
                    this.hasPlayerForceGiveUpForCrewPenalty = false;
                    internalState = CombatZoneInternalState.CANNONS;
                    for (PlayerData p : players) {
                        playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
                    }

                    Handler.initializeEngineStrengths(player, enginesStats);
                    Handler.initializeEngineStrengths(player, cannonsStats);
                    sendCombatZonePhase(2);
                    sendCurrentPlayer = true;
                }
                break;
        }

        if (sendCurrentPlayer) {
            try {
                CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.getCurrentPlayer().getUsername());
                eventCallback.trigger(currentPlayerEvent);
            } catch (Exception e) {
                // Ignore the exception
            }
        }

        super.nextState(GameState.CARDS);
    }

    /**
     * Cleanup and finalization method called when exiting the CombatZoneState.
     * This method performs any necessary cleanup operations before transitioning
     * to the next game state, ensuring proper resource management and state consistency.
     */
    @Override
    public void exit() {
        super.exit();
    }
}