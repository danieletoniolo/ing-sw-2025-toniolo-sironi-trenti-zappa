package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.*;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Slavers;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SlaversState extends State {
    private SlaversInternalState internalState;
    private final Slavers card;
    private final Map<PlayerData, Float> stats;
    private Boolean slaversDefeat;

    /**
     * Enum to represent the internal state of the slavers state.
     */
    private enum SlaversInternalState {
        ENEMY_DEFEAT,
        PENALTY
    }

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card Slavers card associated with the state
     */
    public SlaversState(Board board, EventCallback callback, Slavers card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.internalState = SlaversInternalState.ENEMY_DEFEAT;
        this.card = card;
        this.stats = new HashMap<>();
        this.slaversDefeat = false;
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, List, List)} to use double engines
     * in this state.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        switch (type) {
            case 0 -> throw new IllegalStateException("Cannot use double engines in this state");
            case 1 -> {
                if (internalState != SlaversInternalState.ENEMY_DEFEAT) {
                    throw new IllegalStateException("Use cannon not allowed in this state");
                }
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                this.stats.merge(player, player.getSpaceShip().getCannonsStrength(IDs), Float::sum);
                eventCallback.trigger(event);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }
    }

    /**
     * Implementation of the {@link State #loseCrew(PlayerData, int, List)} to set the crew to lose in
     * order to serve the penalty.
     * @throws IllegalArgumentException if the type is not 0, 1 or 2.
     */
    @Override
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> cabinsID) throws IllegalStateException {
        switch (type) {
            case 0 -> throw new IllegalStateException("No goods to remove in this state");
            case 1 -> throw new IllegalStateException("No batteries to remove in this state");
            case 2 -> {
                Event event = Handler.loseCrew(player, cabinsID, card.getCrewLost());
                eventCallback.trigger(event);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
    }

    /**
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        for (PlayerData player : super.players) {
            SpaceShip ship = player.getSpaceShip();
            float initialStrength = ship.getSingleCannonsStrength();
            if (ship.hasPurpleAlien()) {
                initialStrength += SpaceShip.getAlienStrength();
            }
            stats.put(player, initialStrength);
        }
        super.entry();
    }

    /**
     * Execute the state
     * @param player PlayerData of the player to play
     * @throws IllegalStateException if acceptCredits not set, crewLost not set
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        SpaceShip spaceShip = player.getSpaceShip();

        switch (internalState) {
            case ENEMY_DEFEAT:
                int cardValue = card.getCannonStrengthRequired();
                if (stats.get(player) > cardValue) {
                    slaversDefeat = true;
                } else if (stats.get(player) < cardValue) {
                    slaversDefeat = false;
                } else {
                    slaversDefeat = null;
                }

                EnemyDefeat enemyEvent = new EnemyDefeat(player.getUsername(), Boolean.TRUE.equals(slaversDefeat));
                eventCallback.trigger(enemyEvent);
            case PENALTY:
                if (slaversDefeat != null && slaversDefeat) {
                    if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
                        player.addCoins(card.getCredit());

                        UpdateCoins coinsEvent = new UpdateCoins(player.getUsername(), player.getCoins());
                        eventCallback.trigger(coinsEvent);
                    }
                    super.execute(player);
                } else if (slaversDefeat != null) {
                    /*
                       TODO:
                        I think we need to check if the player has enough crew members before going to the penalty
                        So that if the player has not enough crew members we send the lose event without making the player
                        send an unnecessary loseCrew
                     */

                    if (spaceShip.getCrewNumber() <= card.getCrewLost()) {
                        PlayerLost lostEvent = new PlayerLost();
                        eventCallback.trigger(lostEvent, player.getUUID());
                    } else {
                        // TODO: Due to the change of crewLoss to List<Integer> we need to change the event
                        //AddLoseCrew crewEvent = new AddLoseCrew(player.getUsername(), false, crewLoss);
                        //eventCallback.trigger(crewEvent);
                    }
                    playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                }
                internalState = SlaversInternalState.ENEMY_DEFEAT;
                break;
        }

        try {
            CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.getCurrentPlayer().getUsername());
            eventCallback.trigger(currentPlayerEvent);
        }
        catch(Exception e) {
            // Ignore the exception
        }

        super.nextState(GameState.CARDS);
    }

    @Override
    public void exit() throws IllegalStateException{
        int flightDays = card.getFlightDays();
        PlayerStatus status;
        for (PlayerData p : players) {
            status = playersStatus.get(p.getColor());
            if (status == PlayerStatus.PLAYED) {
                board.addSteps(p, -flightDays);

                MoveMarker stepsEvent = new MoveMarker(p.getUsername(), p.getStep());
                eventCallback.trigger(stepsEvent);
            } else if (status == PlayerStatus.WAITING || status == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
        super.exit();
    }
}
