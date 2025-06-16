package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.event.game.serverToClient.player.EnemyDefeat;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Smugglers;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmugglersState extends State {
    private final Smugglers card;
    private SmugglerInternalState internalState;

    private final Map<PlayerData, Float> cannonStrength;
    private int currentPenaltyLoss;

    /**
     * Enum to represent the internal state of the smugglers state.
     */
    private enum SmugglerInternalState {
        DEFAULT,
        GOODS_REWARD,
        GOODS_PENALTY,
        BATTERIES_PENALTY
    }

    public SmugglersState(Board board, EventCallback callback, Smugglers card, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        this.card = card;
        this.cannonStrength = new HashMap<>();
        this.internalState = SmugglerInternalState.DEFAULT;
        this.currentPenaltyLoss = card.getGoodsLoss();
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, List, List)} to use the extra strength
     * of the double cannons.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, List<Integer> IDs, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
        switch (type) {
            case 0 -> throw new IllegalStateException("Cannot use double engines in this state");
            case 1 -> {
                if (internalState == SmugglerInternalState.GOODS_PENALTY) {
                    throw new IllegalStateException("There is a penalty to serve.");
                }
                Event event = Handler.useExtraStrength(player, type, IDs, batteriesID);
                this.cannonStrength.merge(player, player.getSpaceShip().getCannonsStrength(IDs), Float::sum);
                eventCallback.trigger(event);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }
    }

    /**
     * Implementation of {@link State#setGoodsToExchange(PlayerData, List)} to set the goods the player wants to exchange;
     * @throws IllegalStateException if we cannot exchange goods, there is a penalty to serve.
     * @throws IllegalArgumentException if the storage ID is invalid, if the goods to get are not in the planet selected
     * or if the goods to leave are not in the storage.
     */
    @Override
    public void setGoodsToExchange(PlayerData player, List<Triplet<List<Good>, List<Good>, Integer>> exchangeData) throws IllegalStateException {
        if (internalState != SmugglerInternalState.GOODS_REWARD) {
            throw new IllegalStateException("Cannot exchange goods, there is a penalty to serve.");
        }

        Event exchangeGoodsEvent = Handler.exchangeGoods(player, exchangeData, card.getGoodsReward());
        eventCallback.trigger(exchangeGoodsEvent);
    }

    /**
     * Implementation of {@link State#swapGoods(PlayerData, int, int, List, List)} to swap the goods between two storage.
     * @throws IllegalStateException if we cannot exchange goods, there is a penalty to serve.
     * @throws IllegalArgumentException if the storage ID is invalid, if the goods to get are not in the planet selected
     * or if the goods to leave are not in the storage.
     */
    @Override
    public void swapGoods(PlayerData player, int storageID1, int storageID2, List<Good> goods1to2, List<Good> goods2to1) throws IllegalStateException {
        if (internalState != SmugglerInternalState.GOODS_REWARD) {
            throw new IllegalStateException("Cannot exchange goods, there is a penalty to serve.");
        }

        Event goodsSwappedEvent = Handler.swapGoods(player, storageID1, storageID2, goods1to2, goods2to1);
        eventCallback.trigger(goodsSwappedEvent);
    }

    /**
     * Implementation of the {@link State#setPenaltyLoss(PlayerData, int, List)} to set the goods to lose in
     * order to serve the penalty and if there are not enough goods to lose, set the batteries to lose.
     * @throws IllegalArgumentException if the type is not 0, 1 or 2.
     */
    @Override
    public void setPenaltyLoss(PlayerData player, int type, List<Integer> penaltyLoss) throws IllegalStateException {
        switch (type) {
            case 0 -> {
                // Check if there is a penalty to serve
                if (internalState != SmugglerInternalState.GOODS_PENALTY) {
                    throw new IllegalStateException("There is no penalty to serve.");
                }
                Event event = Handler.loseGoods(player, penaltyLoss, currentPenaltyLoss);
                eventCallback.trigger(event);
                currentPenaltyLoss -= penaltyLoss.size();
            }
            case 1 -> {
                // Check if there is penalty to serve
                if (internalState != SmugglerInternalState.BATTERIES_PENALTY) {
                    throw new IllegalStateException("There is no penalty to serve.");
                }
                Event event = Handler.loseBatteries(player, penaltyLoss, currentPenaltyLoss);
                currentPenaltyLoss = card.getGoodsLoss();
                eventCallback.trigger(event);
            }
            case 2 -> throw new IllegalStateException("No crew to lose in this state");
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0, 1 or 2.");
        }
    }

    @Override
    public void entry() {
        for (PlayerData player : players) {
            SpaceShip ship = player.getSpaceShip();
            float initialStrength = ship.getSingleCannonsStrength();
            if (ship.hasPurpleAlien()) {
                initialStrength += SpaceShip.getAlienStrength();
            }
            cannonStrength.put(player, initialStrength);
        }
        super.entry();
    }

    /**
     * @throws IllegalStateException if the player has not set enough data to execute the state or if the internal state is not set correctly
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException, IllegalStateException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        switch (internalState) {
            case DEFAULT:
                // Check if the player has enough cannon strength to beat the card
                if (cannonStrength.get(player) > card.getCannonStrengthRequired()) {
                    internalState = SmugglerInternalState.GOODS_REWARD;

                    EnemyDefeat enemyDefeat = new EnemyDefeat(player.getUsername(), true);
                    eventCallback.trigger(enemyDefeat);
                } else if (cannonStrength.get(player) == card.getCannonStrengthRequired()) {
                    // Set the player as played
                    playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
                } else {
                    this.internalState = SmugglerInternalState.GOODS_PENALTY;
                }
                break;
            case GOODS_REWARD:
                playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                super.played = true;
                break;
            case GOODS_PENALTY:
                if (currentPenaltyLoss > 0) {
                    internalState = SmugglerInternalState.BATTERIES_PENALTY;
                }
                break;
            case BATTERIES_PENALTY:
                playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
                this.internalState = SmugglerInternalState.DEFAULT;
                break;
            default:
                throw new IllegalStateException("Unknown internal state" + internalState);
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
    public void exit() throws IllegalStateException {
        super.exit();
        for (PlayerData p : players) {
            if (playersStatus.get(p.getColor()) == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                board.addSteps(p, -flightDays);

                MoveMarker stepsEvent = new MoveMarker(p.getUsername(), p.getStep());
                eventCallback.trigger(stepsEvent);
            }
        }
    }
}
