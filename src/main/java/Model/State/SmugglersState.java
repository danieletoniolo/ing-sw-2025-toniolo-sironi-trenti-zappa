package Model.State;

import Model.Cards.Smugglers;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.ExchangeableGoods;
import Model.State.interfaces.UsableCannon;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;

enum SmugglerInternalState {
    DEFAULT,
    PENALTY
}

public class SmugglersState extends State implements UsableCannon, ExchangeableGoods {
    private final Smugglers card;
    private SmugglerInternalState internalState;

    private Map<PlayerData, Float> cannonStrength;
    private ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData;
    private ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard;
    private ArrayList<Pair<Integer, Integer>> crewToLose;

    public SmugglersState(ArrayList<PlayerData> players, Smugglers card) {
        super(players);
        this.card = card;
        this.cannonStrength = new HashMap<>();
        this.internalState = SmugglerInternalState.DEFAULT;
        this.exchangeData = null;
        this.goodsToDiscard = null;
        this.crewToLose = null;
    }

    /**
     * @throws IllegalStateException if we are in the penalty state
     */
    public void useCannon(PlayerData player, Float strength) throws IllegalStateException {
        if (internalState == SmugglerInternalState.PENALTY) {
            throw new IllegalStateException("There is a penalty to serve.");
        }
        float oldCannonStrength = cannonStrength.get(player);
        cannonStrength.replace(player, oldCannonStrength + strength);
    }

    /**
     * @throws IllegalStateException if we are in the penalty state
     */
    public void setGoodsToExchange(PlayerData player, ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData) throws IllegalStateException {
        if (internalState == SmugglerInternalState.PENALTY) {
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
        if (internalState == SmugglerInternalState.DEFAULT) {
            throw new IllegalStateException("There is no penalty to serve yet.");
        }
        this.goodsToDiscard = goodsToDiscard;
    }

    /**
     * Set the crew to lose in order to serve the penalty
     * @param crewToLose ArrayList of (in order) the cabin ID and the number if crew members to lose
     */
    public void setCrewToLose(ArrayList<Pair<Integer, Integer> > crewToLose) {
        if (internalState == SmugglerInternalState.DEFAULT) {
            throw new IllegalStateException("There is no penalty to serve yet.");
        }
        this.crewToLose = crewToLose;
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
                switch (internalState) {
                    case DEFAULT:
                            // Check if the player has enough cannon strength to beat the card
                            if (cannonStrength.get(player) > card.getCannonStrengthRequired()) {
                                // If the player has enough cannon strength and want to exchange goods we execute the exchange
                                if (exchangeData != null) {
                                    for (Triplet<ArrayList<Good>, ArrayList<Good>, Integer> triplet : exchangeData) {
                                        SpaceShip ship = p.getValue0().getSpaceShip();
                                        ship.exchangeGood(triplet.getValue0(), triplet.getValue1(), triplet.getValue2());
                                    }
                                }
                                // Set the player as played
                                p.setAt1(PlayerStatus.PLAYED);
                                // Set the state as finished
                                super.played = true;
                            } else if (cannonStrength.get(player) == card.getCannonStrengthRequired()) {
                                // Set the player as played
                                p.setAt1(PlayerStatus.SKIPPED);
                            } else {
                                // If the player doesn't have enough cannon strength we can't exchange goods
                                this.exchangeData = null;
                                // Change the internal state to PENALTY
                                this.internalState = SmugglerInternalState.PENALTY;
                            }
                        case PENALTY:
                            // If the player has not set the goods to discard, we throw an exception
                            if (goodsToDiscard == null && crewToLose == null) {
                                throw new IllegalStateException("No goods or crew to discard set");
                            }

                            SpaceShip ship = p.getValue0().getSpaceShip();

                            if (goodsToDiscard != null) {
                                // Check that the selected goods to discard are the most valuable
                                // TODO: We could optimize this by making this check in the view
                                PriorityQueue<Good> goodsToDiscardQueue = new PriorityQueue<>(Comparator.comparingInt(Good::getValue).reversed());
                                for (Pair<ArrayList<Good>, Integer> pair : goodsToDiscard) {
                                    goodsToDiscardQueue.addAll(pair.getValue0());
                                }
                                PriorityQueue<Good> mostValuableGoods = new PriorityQueue<>(new ArrayList<>(ship.getGoods()));
                                for (int i = 0; i < goodsToDiscardQueue.size(); i++) {
                                    if (goodsToDiscardQueue.peek().getValue() != mostValuableGoods.peek().getValue()) {
                                        throw new IllegalStateException("The goods to discard are not the most valuable");
                                    }
                                    goodsToDiscardQueue.poll();
                                    mostValuableGoods.poll();
                                }

                                // Remove the goods from the ship
                                for (Pair<ArrayList<Good>, Integer> pair : goodsToDiscard) {
                                    ship.exchangeGood(null, pair.getValue0(), pair.getValue1());
                                }
                            }

                            // Remove the crew to lose if there is any
                            if (crewToLose != null) {
                                for (Pair<Integer, Integer> pair : crewToLose) {
                                    ship.removeCrewMember(pair.getValue1(), pair.getValue0());
                                }
                            }

                            // Reset the goods to discard
                            this.goodsToDiscard = null;
                            // Set the player as played
                            p.setAt1(PlayerStatus.SKIPPED);
                            // Change back the internal state to DEFAULT
                            this.internalState = SmugglerInternalState.DEFAULT;
                        default:
                            throw new IllegalStateException("Unknown internal state" + internalState);
                    }
                }
            }
        }

    @Override
    public void exit() throws IllegalStateException {
        super.exit();
        for (Pair<PlayerData, PlayerStatus> p : players) {
            if (p.getValue1() == PlayerStatus.PLAYED) {
                int flightDays = card.getFlightDays();
                p.getValue0().addSteps(-flightDays);
            }
        }
    }
}
