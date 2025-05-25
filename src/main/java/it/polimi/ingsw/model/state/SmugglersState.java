package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.cards.Smugglers;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.spaceship.Storage;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.*;
import org.javatuples.Triplet;

import java.util.*;


public class SmugglersState extends State {
    private final Smugglers card;
    private SmugglerInternalState internalState;

    private final Map<PlayerData, Float> cannonStrength;
    private List<Triplet<List<Good>, List<Good>, Integer>> exchangeData;
    private List<Integer> goodsToDiscard;
    private List<Integer> batteriesToDiscard;

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
        this.exchangeData = null;
        this.goodsToDiscard = null;
        this.batteriesToDiscard = null;
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

                // Use the energy to power the cannon
                SpaceShip ship = player.getSpaceShip();
                for (Integer batteryID : batteriesID) {
                    ship.useEnergy(batteryID);
                }

                // Update the cannon strength stats
                float oldCannonStrength = cannonStrength.get(player);
                this.cannonStrength.replace(player, oldCannonStrength + ship.getCannonsStrength(IDs));

                CannonsUsed useCannonsEvent = new CannonsUsed(player.getUsername(), IDs, (ArrayList<Integer>) batteriesID);
                eventCallback.trigger(useCannonsEvent);
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
        for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
            Storage storage;
            // Check that the storage exists
            try {
                SpaceShip ship = player.getSpaceShip();
                storage = ship.getStorage(triplet.getValue2());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid storage ID: " + triplet.getValue2());
            }
            // Check that the goods to get are in the planet selected
            for (Good good : triplet.getValue0()) {
                if (!card.getGoodsReward().contains(good)) {
                    throw new IllegalArgumentException ("The good " + good + " the player want to get is not in the smuggler reward");
                }
                // Check if there is dangerous goods
                if (good.getColor() == GoodType.RED && !storage.isDangerous()) {
                    throw new IllegalArgumentException ("The good " + good + " is dangerous and the storage is not dangerous");
                }
            }
            // Check that the goods to leave are in the storage
            for (Good good : triplet.getValue1()) {
                if (!storage.hasGood(good)) {
                    throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + triplet.getValue2());
                }
            }
            // Check that we can store the goods in the storage
            if (storage.getGoodsCapacity() + triplet.getValue1().size() < triplet.getValue0().size()) {
                throw new IllegalArgumentException ("The storage " + triplet.getValue2() + " does not have enough space to store the goods");
            }
        }
        this.exchangeData = exchangeData;
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
        // Check that the storage exists
        SpaceShip ship = player.getSpaceShip();
        Storage storage1, storage2;
        try {
            storage1 = ship.getStorage(storageID1);
            storage2 = ship.getStorage(storageID2);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid storage ID: " + storageID1 + " or " + storageID2);
        }
        // Check that the goods to leave are in the storage 1
        for (Good good : goods1to2) {
            if (!storage1.hasGood(good)) {
                throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + storageID1);
            }
        }
        // Check that the goods to leave are in the storage 2
        for (Good good : goods2to1) {
            if (!storage2.hasGood(good)) {
                throw new IllegalArgumentException ("The Good " + good + " the player want to leave is not in storage " + storageID2);
            }
        }
        // Check that we can store the goods in the storage 1
        if (storage1.getGoodsCapacity() + goods1to2.size() < goods2to1.size()) {
            throw new IllegalArgumentException ("The storage " + storageID1 + " does not have enough space to store the goods");
        }
        // Check that we can store the goods in the storage 2
        if (storage2.getGoodsCapacity() + goods2to1.size() < goods1to2.size()) {
            throw new IllegalArgumentException ("The storage " + storageID2 + " does not have enough space to store the goods");
        }
        // Swap the goods
        ship.exchangeGood(goods1to2, goods2to1, storageID1);
        ship.exchangeGood(goods2to1, goods1to2, storageID2);

        GoodsSwapped goodsSwappedEvent = new GoodsSwapped(player.getUsername(), storageID1, storageID2, goods1to2.stream().map(Good::getValue).toList(), goods2to1.stream().map(Good::getValue).toList());
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
                Map <Integer, Integer> goodsMap = new HashMap<>();
                for (int storageID : penaltyLoss) {
                    goodsMap.merge(storageID, 1, Integer::sum);
                }
                // Check that the selected goods to discard are the most valuable
                PriorityQueue<Good> goodsToDiscardQueue = new PriorityQueue<>(Comparator.comparingInt(Good::getValue).reversed());
                for (int storageID : goodsMap.keySet()) {
                    for (int i = 0; i < goodsMap.get(storageID); i++) {
                        Good good = player.getSpaceShip().getStorage(storageID).peekGood(i);
                        if (good == null) {
                            throw new IllegalStateException("Not enough goods in storage " + storageID);
                        }
                        goodsToDiscardQueue.add(good);
                    }
                }
                PriorityQueue<Good> mostValuableGoods = new PriorityQueue<>(player.getSpaceShip().getGoods());
                for (int i = 0; i < goodsToDiscardQueue.size(); i++) {
                    if (goodsToDiscardQueue.peek().getValue() != mostValuableGoods.peek().getValue()) {
                        throw new IllegalStateException("The goods to discard are not the most valuable");
                    }
                    goodsToDiscardQueue.poll();
                    mostValuableGoods.poll();
                }
                // Finally we can set the goods to discard
                this.goodsToDiscard = penaltyLoss;
            }
            case 1 -> {
                // Check if there is penalty to serve
                if (internalState != SmugglerInternalState.BATTERIES_PENALTY) {
                    throw new IllegalStateException("There is no penalty to serve.");
                }
                // Check if there are the provided number of batteries in the provided batteries slots.
                Map<Integer, Integer> batteriesMap = new HashMap<>();
                for (int batteryID : penaltyLoss) {
                    batteriesMap.merge(batteryID, 1, Integer::sum);
                }
                SpaceShip ship = player.getSpaceShip();
                for (int batteryID : batteriesMap.keySet()) {
                    if (ship.getBattery(batteryID).getEnergyNumber() < batteriesMap.get(batteryID)) {
                        throw new IllegalStateException("Not enough energy in battery " + batteryID);
                    }
                }
                // Check if the number of batteries to remove is equal to the number of batteries required to lose
                // The number of batteries to lose is the number of goods to discard minus the number of goods already discarded
                if (penaltyLoss.size() != card.getGoodsLoss() - goodsToDiscard.size()) {
                    throw new IllegalStateException("The batteries removed is not equal to the batteries required to lose");
                }
                this.batteriesToDiscard = penaltyLoss;
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
    }

    /**
     * @throws IllegalStateException if the player has not set enough data to execute the state or if the internal state is not set correctly
     */
    @Override
    public void execute(PlayerData player) throws NullPointerException, IllegalStateException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        SpaceShip ship = player.getSpaceShip();
        switch (internalState) {
            case DEFAULT:
                    // Check if the player has enough cannon strength to beat the card
                    if (cannonStrength.get(player) > card.getCannonStrengthRequired()) {
                        internalState = SmugglerInternalState.GOODS_REWARD;
                        // TODO: Notify the player that we won the fight
                    } else if (cannonStrength.get(player) == card.getCannonStrengthRequired()) {
                        // Set the player as played
                        playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
                    } else {
                        // If the player doesn't have enough cannon strength we can't exchange goods
                        this.exchangeData = null;
                        // Change the internal state to GOODS_PENALTY
                        this.internalState = SmugglerInternalState.GOODS_PENALTY;
                    }
                    break;
                case GOODS_REWARD:
                    // If the player has enough cannon strength and want to exchange goods we execute the exchange
                    if (exchangeData != null) {
                        for (Triplet<List<Good>, List<Good>, Integer> triplet : exchangeData) {
                            ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
                        }

                        List<Triplet<List<Integer>, List<Integer>, Integer>> convertedData = exchangeData.stream()
                                .map(t -> new Triplet<>(
                                        t.getValue0().stream()
                                                .map(Good::getValue)
                                                .toList(),
                                        t.getValue1().stream()
                                                .map(Good::getValue)
                                                .toList(),
                                        t.getValue2()))
                                .toList();
                        UpdateGoodsExchange exchangeGoodsEvent = new UpdateGoodsExchange(player.getUsername(), convertedData);
                        eventCallback.trigger(exchangeGoodsEvent);
                    }
                    // Set the player as played
                    playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                    // Set the state as finished
                    CardPlayed cardPlayedEvent = new CardPlayed();
                    eventCallback.trigger(cardPlayedEvent);
                    super.played = true;
                    break;
                case GOODS_PENALTY:
                    // If the player has not set the goods to discard, we throw an exception
                    if (goodsToDiscard == null) {
                        throw new IllegalStateException("No goods to discard set");
                    }
                    // Remove the goods from the ship
                    for (int storageID : goodsToDiscard) {
                        ship.pollGood(storageID);
                    }
                    // TODO: EVENT EXCHANGEGOODS
                    if (goodsToDiscard.size() < card.getGoodsLoss()) {
                        internalState = SmugglerInternalState.BATTERIES_PENALTY;
                    }
                    break;
                case BATTERIES_PENALTY:
                    // If the player has not set the batteries to discard, we throw an exception
                    if (batteriesToDiscard == null) {
                        throw new IllegalStateException("No batteries to discard set");
                    }
                    for (int batteriesID : batteriesToDiscard)       {
                        ship.useEnergy(batteriesID);
                    }
                    // Reset the goods to discard
                    this.goodsToDiscard = null;
                    // Reset the crew to lose
                    this.batteriesToDiscard = null;
                    // Set the player as SKIPPED (The player to be set as PLAYED is the one that beats the card)
                    playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
                    // Change back the internal state to DEFAULT
                    this.internalState = SmugglerInternalState.DEFAULT;
                    break;
                default:
                    throw new IllegalStateException("Unknown internal state" + internalState);
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

                MoveMarker stepsEvent = new MoveMarker(p.getUsername(), flightDays);
                eventCallback.trigger(stepsEvent);
            }
        }
    }
}
