package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
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
    private final Map<PlayerData, Float> cannonStrength;
    private boolean hasPlayerForceGiveUp;
    private Boolean slaversDefeat = false;

    /**
     * Enum to represent the internal state of the slavers state.
     */
    enum SlaversInternalState {
        ENEMY_DEFEAT,
        REWARD,
        PENALTY,
        GIVE_UP
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
        this.cannonStrength = new HashMap<>();
        this.hasPlayerForceGiveUp = false;
        this.slaversDefeat = false;
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, List, List)} to use double engines
     * in this state.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        if (this.hasPlayerForceGiveUp) {
            throw new IllegalStateException("You are forced to give up, you cannot use extra strength");
        }

        switch (type) {
            case 0 -> throw new IllegalStateException("Cannot use double engines in this state");
            case 1 -> {
                if (internalState != SlaversInternalState.ENEMY_DEFEAT) {
                    throw new IllegalStateException("Use cannon not allowed in this state");
                }
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                this.cannonStrength.merge(player, player.getSpaceShip().getCannonsStrength(IDs), Float::sum);
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
        if (this.hasPlayerForceGiveUp) {
            throw new IllegalStateException("You are forced to give up, you cannot set crew to remove");
        }

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
                initialStrength += ship.getAlienStrength(true);
            }
            cannonStrength.put(player, initialStrength);
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
        boolean sendCurrentPlayer = false;

        switch (internalState) {
            case ENEMY_DEFEAT:
                int cannonStrengthRequired = card.getCannonStrengthRequired();

                if (cannonStrength.get(player) > cannonStrengthRequired) {
                    slaversDefeat = true;
                    internalState = SlaversInternalState.REWARD;
                } else if (cannonStrength.get(player) < cannonStrengthRequired) {
                    slaversDefeat = false;
                    internalState = SlaversInternalState.PENALTY;
                } else {
                    slaversDefeat = null;
                    sendCurrentPlayer = true;
                    playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
                }

                EnemyDefeat enemyEvent = new EnemyDefeat(player.getUsername(), slaversDefeat);
                eventCallback.trigger(enemyEvent);
                break;
            case REWARD:
                if (playersStatus.get(player.getColor()) == PlayerStatus.PLAYING) {
                    player.addCoins(card.getCredit());

                    UpdateCoins coinsEvent = new UpdateCoins(player.getUsername(), player.getCoins());
                    eventCallback.trigger(coinsEvent);

                    board.addSteps(player, -card.getFlightDays());
                    MoveMarker stepsEvent = new MoveMarker(player.getUsername(),  player.getModuleStep(board.getStepsForALap()));
                    eventCallback.trigger(stepsEvent);
                }

                for (PlayerData p: players) {
                    super.execute(p);
                }
                break;
            case PENALTY:
                if (spaceShip.getHumanCrewNumber() == 0) {
                    ForcingGiveUp lostEvent = new ForcingGiveUp(player.getUsername(), "You are forced to give up, you have no human crew left");
                    eventCallback.trigger(lostEvent, player.getUUID());
                    internalState = SlaversInternalState.GIVE_UP;
                    this.hasPlayerForceGiveUp = true;
                } else {
                    super.execute(player);
                    sendCurrentPlayer = true;
                    internalState = SlaversInternalState.ENEMY_DEFEAT;
                }
                break;
            case GIVE_UP:
                this.hasPlayerForceGiveUp = false;
                super.execute(player);
                internalState = SlaversInternalState.ENEMY_DEFEAT;
                sendCurrentPlayer = true;
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

    @Override
    public void exit() throws IllegalStateException{
        super.exit();
    }
}
