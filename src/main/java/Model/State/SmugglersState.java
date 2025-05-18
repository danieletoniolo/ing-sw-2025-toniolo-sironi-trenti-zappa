package Model.State;

import Model.Cards.Smugglers;
import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.ExchangeableGoods;

import controller.EventCallback;
import event.game.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;


public class SmugglersState extends State implements ExchangeableGoods {
    private final Smugglers card;
    private SmugglerInternalState internalState;

    private final Map<PlayerData, Float> cannonStrength;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;
    private List<Integer> goodsToDiscard;
    private List<Integer> batteriesToDiscard;

    /**
     * Enum to represent the internal state of the smugglers state.
     */
    private enum SmugglerInternalState {
        DEFAULT,
        GOODS_PENALTY,
        BATTERIES_PENALTY
    }

    public SmugglersState(Board board, EventCallback callback, Smugglers card) {
        super(board, callback);
        this.card = card;
        this.cannonStrength = new HashMap<>();
        this.internalState = SmugglerInternalState.DEFAULT;
        this.exchangeData = null;
        this.goodsToDiscard = null;
        this.batteriesToDiscard = null;
    }

    /**
     * Implementation of the {@link State#useExtraStrength(PlayerData, int, float, List)} to use the extra strength
     * of the double cannons.
     * @throws IllegalArgumentException if the type is not 0 or 1.
     */
    @Override
    public void useExtraStrength(PlayerData player, int type, float strength, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException {
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
                this.cannonStrength.replace(player, oldCannonStrength + strength);

                UseCannons useCannonsEvent = new UseCannons(player.getUsername(), strength, (ArrayList<Integer>) batteriesID);
                eventCallback.trigger(useCannonsEvent);
            }
            default -> throw new IllegalArgumentException("Invalid type: " + type + ". Expected 0 or 1.");
        }

    }

    /**
     * @throws IllegalStateException if we are in the penalty state
     */
    public void setGoodsToExchange(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws IllegalStateException {
        if (internalState == SmugglerInternalState.GOODS_PENALTY) {
            throw new IllegalStateException("There is a penalty to serve.");
        }
        this.exchangeData = exchangeData;
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
                        // If the player has enough cannon strength and want to exchange goods we execute the exchange
                        if (exchangeData != null) {
                            for (Triplet<ArrayList<Good>, ArrayList<Good>, Integer> triplet : exchangeData) {
                                ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
                            }

                            ExchangeGoods exchangeGoodsEvent = new ExchangeGoods(player.getUsername(), exchangeData);
                            eventCallback.trigger(exchangeGoodsEvent);
                        }
                        // Set the player as played
                        playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                        // Set the state as finished

                        CardPlayed cardPlayedEvent = new CardPlayed();
                        eventCallback.trigger(cardPlayedEvent);

                        super.played = true;
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
