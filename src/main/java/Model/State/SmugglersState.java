package Model.State;

import Model.Cards.Smugglers;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import Model.State.interfaces.ExchangeableGoods;
import Model.State.interfaces.UsableCannon;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum smugglerInternalState {
    DEFAULT,
    PENALTY
}

public class SmugglersState extends State implements UsableCannon, ExchangeableGoods {
    private final Smugglers card;
    private smugglerInternalState smugglerInternalState;

    private Map<PlayerData, Float> cannonStrength;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;
    private ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard;

    public SmugglersState(ArrayList<PlayerData> players, Smugglers card) {
        super(players);
        this.card = card;
        this.cannonStrength = new HashMap<>();
        this.smugglerInternalState = Model.State.smugglerInternalState.DEFAULT;
        this.exchangeData = null;
        this.goodsToDiscard = null;
    }

    /**
     * @throws IllegalStateException if we are in the penalty state
     */
    public void useCannon(PlayerData player, float strength) throws IllegalStateException {
        if (smugglerInternalState == Model.State.smugglerInternalState.PENALTY) {
            throw new IllegalStateException("There is a penalty to serve.");
        }
        float oldCannonStrength = cannonStrength.get(player);
        cannonStrength.replace(player, oldCannonStrength + strength);
    }

    /**
     * @throws IllegalStateException if we are in the penalty state
     */
    public void exchangeGoods(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws IllegalStateException {
        if (smugglerInternalState == Model.State.smugglerInternalState.PENALTY) {
            throw new IllegalStateException("There is a penalty to serve.");
        }
        this.exchangeData = exchangeData;
    }

    /**
     * Set the goods to discard for the player in order to serve the penalty
     * @param player the player that has to discard the goods
     * @param goodsToDiscard ArrayList of (in order) the good to discard and the storage id where to take it from
     */
    public void setGoodsToDiscard(PlayerData player, ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard) {
        if (smugglerInternalState == Model.State.smugglerInternalState.DEFAULT) {
            throw new IllegalStateException("There is no penalty to serve yet.");
        }
        this.goodsToDiscard = goodsToDiscard;
    }

    @Override
    public void entry() {
        for (Pair<PlayerData, PlayerStatus> player : players) {
            SpaceShip ship = player.getValue0().getSpaceShip();
            cannonStrength.put(player.getValue0(), ship.getSingleCannonsStrength());
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
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue0().equals(player)) {
                if (p.getValue1() == PlayerStatus.PLAYING) {
                    switch (smugglerInternalState) {
                        case DEFAULT:
                            // Check if the player has enough cannon strength to beat the card
                            if (cannonStrength.get(player) > card.getCannonStrengthRequired()) {
                                // If the player has not set the exchange data, we throw an exception
                                if (exchangeData == null) {
                                    throw new IllegalStateException("No exchange data set");
                                }
                                // If the player has enough cannon strength we can exchange goods
                                for (Triplet<ArrayList<Good>, ArrayList<Good>, Integer> triplet : exchangeData) {
                                    ArrayList<Good> goodsToGet = triplet.getValue0();
                                    ArrayList<Good> goodsToLeave = triplet.getValue1();
                                    int storageId = triplet.getValue2();

                                    Storage storage = player.getSpaceShip().getStorage(storageId);
                                    for (Good good : goodsToGet) {
                                        storage.addGood(good);
                                    }
                                    for (Good good : goodsToLeave) {
                                        storage.removeGood(good);
                                    }
                                }
                                // Set the player as played
                                p.setAt1(PlayerStatus.PLAYED);
                                // Set the state as finished
                                super.played = true;
                            } else {
                                // If the player doesn't have enough cannon strength we can't exchange goods
                                this.exchangeData = null;
                                // Change the internal state to PENALTY
                                this.smugglerInternalState = Model.State.smugglerInternalState.PENALTY;
                            }
                        case PENALTY:
                            // If the player has not setted the goods to discard, we throw an exception
                            if (goodsToDiscard == null) {
                                throw new IllegalStateException("No goods to discard set");
                            }
                            // Discard the goods to serve penalty
                            for (Pair<ArrayList<Good>, Integer> pair : goodsToDiscard) {
                                ArrayList<Good> goodsToDiscard = pair.getValue0();
                                int storageId = pair.getValue1();

                                Storage storage = player.getSpaceShip().getStorage(storageId);
                                for (Good good : goodsToDiscard) {
                                    storage.removeGood(good);
                                }
                            }
                            // Reset the goods to discard
                            this.goodsToDiscard = null;
                            // Set the player as played
                            p.setAt1(PlayerStatus.PLAYED);
                            // Change back the internal state to DEFAULT
                            this.smugglerInternalState = Model.State.smugglerInternalState.DEFAULT;
                        default:
                            throw new IllegalStateException("Unknown internal state");
                    }
                } else {
                    p.setAt1(PlayerStatus.WAITING);
                }
            }
        }
    }

    @Override
    public void exit() throws IllegalStateException {
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                p.getValue0().addSteps(-flightDays);
            } else if (p.getValue1() == PlayerStatus.WAITING || p.getValue1() == PlayerStatus.PLAYING) {
                throw new IllegalStateException("Not all players have played");
            }
        }
    }
}
