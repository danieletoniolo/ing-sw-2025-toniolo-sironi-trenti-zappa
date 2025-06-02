package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.MinPlayer;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.game.serverToClient.player.PlayerLost;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.CombatZone;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import org.javatuples.Pair;

import java.util.*;

public class CombatZoneState extends State {
    private CombatZoneInternalState internalState;
    private final CombatZone card;
    private final Map<PlayerData, Float> enginesStats;
    private final Map<PlayerData, Float> cannonsStats;
    private PlayerData minPlayerEngines;
    private PlayerData minPlayerCannons;
    private PlayerData minPlayerCrew;
    private PlayerData playerBeingHit;
    private final List<List<Pair<Integer, Integer>>> fragments;
    private final Pair<Component, Integer> protectionResult;
    private int hitIndex;

    private int currentPenaltyLoss;

    /**
     * Enum to represent the internal state of the combat zone state.
     */
    private enum CombatZoneInternalState {
        CREW,
        ENGINES,
        CANNONS,
        CREW_PENALTY,
        GOODS_PENALTY,
        BATTERIES_PENALTY,
        HIT_PENALTY
    }

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card CombatZone card associated with the state
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
        this.protectionResult = new Pair<>(null, -1);
        this.hitIndex = 0;
        this.enginesStats = new HashMap<>();
        this.cannonsStats = new HashMap<>();
    }

    /**
     * Execute the subState crew
     */
    private void executeSubStateFlightDays(PlayerData player) {
        int flightDays = card.getFlightDays();
        board.addSteps(player, -flightDays);

        MoveMarker stepsEvent = new MoveMarker(player.getUsername(), player.getStep());
        eventCallback.trigger(stepsEvent);
    }

    /**
     * Implementation of the {@link State#setFragmentChoice(PlayerData, int)} to set the fragment choice.
     */
    @Override
    public void setFragmentChoice(PlayerData player, int fragmentChoice) throws IllegalStateException {
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        if (fragments.isEmpty()) {
            throw new IllegalArgumentException("No fragments available to choose from");
        }
        Event event = Handler.destroyFragment(player, fragments.get(fragmentChoice));
        eventCallback.trigger(event);
        fragments.clear();
    }

    /**
     * Implementation of the {@link State#setProtect(PlayerData, int)} to set whether the player wants to protect or not.
     */
    @Override
    public void setProtect(PlayerData player, int batteryID) throws IllegalStateException, IllegalArgumentException {
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Battery ID not allowed in this state");
        }
        Event event = Handler.protectFromHit(player, protectionResult, batteryID);
        if (event != null) {
            eventCallback.trigger(event);
        }
        event = Handler.checkForFragments(player, fragments);
        if (event != null) {
            eventCallback.trigger(event);
        } else {
            fragments.clear();
        }
    }

    /**
     * Implementation of the {@link State#rollDice(PlayerData)} to roll the dice and set the value in the fight handler.
     */
    @Override
    public void rollDice(PlayerData player) throws IllegalStateException{
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        Event event = Handler.rollDice(player, card.getFires().get(hitIndex), protectionResult);
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
                Event event = Handler.loseGoods(player, penaltyLoss,currentPenaltyLoss);
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
                playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.WAITING);
            }
            case 2 -> {
                if (internalState != CombatZoneInternalState.CREW_PENALTY) {
                    throw new IllegalStateException("There is no penalty to serve.");
                }
                Event event = Handler.loseCrew(player, penaltyLoss, currentPenaltyLoss);
                eventCallback.trigger(event);
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
        MinPlayer minPlayerEvent = null;

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
                    minPlayerEvent = new MinPlayer(player.getUsername());
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
                    minPlayerEvent = new MinPlayer(player.getUsername());
                }
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }

        if (minPlayerEvent != null) {
            eventCallback.trigger(minPlayerEvent);
        }
    }

    /**
     * Entry: Add the default stats to the player
     */
    @Override
    public void entry() {
        SpaceShip ship;
        for (PlayerData player : super.players) {
            ship = player.getSpaceShip();
            if (minPlayerCrew == null || minPlayerCrew.getSpaceShip().getCrewNumber() > ship.getCrewNumber()) {
                minPlayerCrew = player;
            }

            enginesStats.merge(player, (float) ship.getSingleEnginesStrength(), Float::sum);
            if (ship.hasBrownAlien()) {
                enginesStats.merge(player, SpaceShip.getAlienStrength(), Float::sum);
            }
            if (minPlayerEngines == null || minPlayerEngines.getSpaceShip().getSingleEnginesStrength() > ship.getSingleEnginesStrength()) {
                minPlayerEngines = player;
            }

            cannonsStats.merge(player, ship.getSingleCannonsStrength(), Float::sum);
            if (ship.hasPurpleAlien()) {
                cannonsStats.merge(player, SpaceShip.getAlienStrength(), Float::sum);
            }
            if (minPlayerCannons == null || minPlayerCannons.getSpaceShip().getSingleCannonsStrength() > ship.getSingleCannonsStrength()) {
                minPlayerCannons = player;
            }
        }
        if (card.getCardLevel() == 1) {
            playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
            for (PlayerData player : players) {
                playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
            }
        }
    }

    /**
     * Execute one state each of teh cards
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        switch (internalState) {
            case CREW:
                if (card.getCardLevel() == 2) {
                    internalState = CombatZoneInternalState.HIT_PENALTY;
                    playerBeingHit = minPlayerCannons;
                } else {
                    executeSubStateFlightDays(minPlayerCrew);
                    for (PlayerData p : players) {
                        playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
                    }
                    internalState = CombatZoneInternalState.ENGINES;
                }
                break;
            case ENGINES:
                super.execute(player);
                if (players.indexOf(player) == players.size() - 1) {
                    playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
                    if (card.getCardLevel() == 2) {
                        internalState = CombatZoneInternalState.GOODS_PENALTY;
                    } else {
                        internalState = CombatZoneInternalState.CREW_PENALTY;
                    }
                }
                break;
            case GOODS_PENALTY:
                if (currentPenaltyLoss > 0 && player.getSpaceShip().getGoodsValue() > 0) {
                    throw new IllegalStateException("Player has not set the goods to lose");
                }
                if (currentPenaltyLoss > 0) {
                    internalState = CombatZoneInternalState.BATTERIES_PENALTY;
                } else {
                    internalState = CombatZoneInternalState.CREW;
                    playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
                }
                break;
            case BATTERIES_PENALTY:
                if (currentPenaltyLoss > 0 && player.getSpaceShip().getEnergyNumber() > 0) {
                    throw new IllegalStateException("Player has not set the batteries to lose");
                }
                internalState = CombatZoneInternalState.CREW;
                playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
                break;
            case CREW_PENALTY:
                if (currentPenaltyLoss > 0) {
                    Event event = new PlayerLost();
                    eventCallback.trigger(event, player.getUUID());
                } else {
                    internalState = CombatZoneInternalState.CANNONS;
                    for (PlayerData p : players) {
                        playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
                    }
                }
                break;
            case HIT_PENALTY:
                hitIndex++;
                if (hitIndex >= card.getFires().size()) {
                    playersStatus.replace(playerBeingHit.getColor(), PlayerStatus.PLAYED);
                }
            case CANNONS:
                super.execute(player);
                if (players.indexOf(player) == players.size() - 1) {
                    if (card.getCardLevel() == 2) {
                        executeSubStateFlightDays(minPlayerCannons);
                        for (PlayerData p : players) {
                            playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
                        }
                        internalState = CombatZoneInternalState.ENGINES;
                    } else {
                        playerBeingHit = minPlayerCannons;
                        playersStatus.replace(minPlayerCannons.getColor(), PlayerStatus.PLAYING);
                        internalState = CombatZoneInternalState.HIT_PENALTY;
                    }
                }
                break;
        }
        super.nextState(GameState.CARDS);
    }
}