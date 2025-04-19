package Model.State;

import Model.Cards.CombatZone;
import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.*;
import org.javatuples.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

enum CombatZoneInternalState {
    CREW(0),
    ENGINES(1),
    CANNONS(2);

    private final int index;

    CombatZoneInternalState(int index) {
        this.index = index;
    }

    public int getIndex(int level) {
        int out = this.index;
        if (level == 2) {
            if (index == 0) {
                out = 2;
            } else if (index == 2) {
                out = 0;
            }
        }
        return out;
    }
}


public class CombatZoneState extends State implements Fightable, ChoosableFragment, RemovableCrew, UsableCannon, UsableEngine, DiscardableGoods {
    private CombatZoneInternalState internalState;
    private final CombatZone card;
    private final ArrayList<Map<PlayerData, Float>> stats;
    private PlayerData minPlayerEngines;
    private PlayerData minPlayerCannons;
    private PlayerData minPlayerCrew;
    private ArrayList<Pair<Integer, Integer>> crewLoss;
    private ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard;
    private final FightHandler fightHandler;

    /**
     * Constructor whit players and card
     * @param board The board associated with the game
     * @param card CombatZone card associated with the state
     */
    public CombatZoneState(Board board, CombatZone card) {
        super(board);
        this.card = card;
        this.stats = new ArrayList<>(3);
        this.internalState = CombatZoneInternalState.CREW;
        this.minPlayerCannons = null;
        this.minPlayerEngines = null;
        this.minPlayerCrew = null;
        this.crewLoss = null;
        this.goodsToDiscard = null;
        this.fightHandler = new FightHandler();
    }

    /**
     * Transition to the next state
     */
    private void transition() {
        this.internalState = switch (internalState) {
            case CREW -> card.getCardLevel() == 2 ? CombatZoneInternalState.CANNONS : CombatZoneInternalState.ENGINES;
            case ENGINES -> card.getCardLevel() == 2 ? CombatZoneInternalState.CREW : CombatZoneInternalState.CANNONS;
            case CANNONS -> card.getCardLevel() == 2 ? CombatZoneInternalState.ENGINES : CombatZoneInternalState.CREW;
        };
        this.crewLoss = null;
        this.goodsToDiscard = null;
    }

    /**
     * Add stats to the player
     * @param statsType type of stats to add
     * @param player player to add to the stats
     * @param value value to add to the stats
     */
    private void addDefaultStats(CombatZoneInternalState statsType, PlayerData player, Float value) {
        stats.get(statsType.getIndex(card.getCardLevel())).merge(player, value, Float::sum);
    }

    /**
     * Add stats to the player, and if all the player have set their stats, find the player with the lowest stats
     * @param player player to add to the stats
     * @param value value to add to the stats
     */
    private void addStats(PlayerData player, Float value) {
        addDefaultStats(internalState, player, value);
    }

    /**
     * Get the minimum player for the engines or cannons
     * @return the player with the minimum stats
     */
    private PlayerData getMinPlayer() {
        AtomicReference<PlayerData> out = new AtomicReference<>();

        stats.get(internalState.getIndex(card.getCardLevel())).entrySet().stream().min(this::comparePlayers).ifPresent(entry -> {
            out.set(entry.getKey());
        });

        return out.get();
    }

    /**
     * Compare players, if the values are the same, the player with the lowest position is chosen
     * @param e1 player 1
     * @param e2 player 2
     * @return comparison between the two players
     */
    private int comparePlayers(Map.Entry<PlayerData, Float> e1, Map.Entry<PlayerData, Float> e2) {
        int valueComparison = Float.compare(e1.getValue(), e2.getValue());
        if (valueComparison != 0) {
            return valueComparison;
        }
        int pos1 = super.getPlayerPosition(e1.getKey());
        int pos2 = super.getPlayerPosition(e2.getKey());
        return Integer.compare(pos1, pos2);
    }

    /**
     * Execute the subState crew
     */
    private void executeSubStateFlightDays(PlayerData player) {
        int flightDays = card.getFlightDays();
        board.addSteps(player, -flightDays);
    }

    /**
     * Execute the subState engines
     * @param player player to execute
     */
    private void executeSubStateRemoveCrew(PlayerData player) throws IllegalArgumentException {
        int crewLost = card.getLost();

        SpaceShip spaceShip = minPlayerEngines.getSpaceShip();
        if (spaceShip.getCrewNumber() <= crewLost) {
            player.setGaveUp(true);
        } else {
            for (Pair<Integer, Integer> cabin : crewLoss) {
                spaceShip.removeCrewMember(cabin.getValue0(), cabin.getValue1());
            }
        }

        fightHandler.initialize(0);
    }

    private void executeSubStateHits() throws IndexOutOfBoundsException {
        int currentHitIndex = fightHandler.getHitIndex();
        if (currentHitIndex >= card.getFires().size()) {
            throw new IndexOutOfBoundsException("Hit index out of bounds");
        }

        fightHandler.executeFight(minPlayerCannons.getSpaceShip(), () -> card.getFires().get(currentHitIndex));
    }

    private void executeSubStateRemoveGoods(PlayerData player) throws IllegalStateException {
        // If the player has not set the goods to discard, we throw an exception
        if (goodsToDiscard == null && crewLoss == null) {
            throw new IllegalStateException("No goods or crew to discard set");
        }

        SpaceShip ship = player.getSpaceShip();

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
        if (crewLoss != null) {
            for (Pair<Integer, Integer> pair : crewLoss) {
                ship.removeCrewMember(pair.getValue1(), pair.getValue0());
            }
        }

        // Reset the goods to discard
        this.goodsToDiscard = null;
        // Set the player as played
        playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice fragment choice
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.ENGINES && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        fightHandler.setFragmentChoice(fragmentChoice);
    }

    /**
     * Set the use energy
     * @param protect_ use energy
     * @param batteryID_ battery ID
     * @throws IllegalStateException if not in the right state in order to do the action
     * @throws IllegalArgumentException if batteryID_ is null and protect_ is true
     */
    public void setProtect(boolean protect_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException {
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.ENGINES && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Battery ID not allowed in this state");
        }
        fightHandler.setProtect(protect_, batteryID_);
    }

    /**
     * Set the dice
     * @param dice dice
     */
    public void setDice(int dice) throws IllegalStateException{
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.ENGINES && card.getCardLevel() == 2)) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        fightHandler.setDice(dice);
    }

    /**
     * Set the cabins ID
     * @param cabinsID Map of cabins ID and number of crew removed for cabins
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setCrewLoss(ArrayList<Pair<Integer, Integer>> cabinsID) throws IllegalStateException {
        if (internalState != CombatZoneInternalState.ENGINES) {
            throw new IllegalStateException("setCabinsID not allowed in this state");
        }

        int crewRemoved = 0;
        for (Pair<Integer, Integer> cabin : cabinsID) {
            crewRemoved += cabin.getValue1();
        }

        if (crewRemoved != card.getLost()) {
            throw new IllegalStateException("The crew removed is not equal to the crew lost");
        }
        this.crewLoss = cabinsID;
    }

    /**
     * Use the cannon with a given strength
     * @param player PlayerData of the player using the cannon
     * @param strength Strength of the cannon to be used
     * @param batteriesID List of Integers representing the batteryID from which we use the energy to use the cannon
     * @throws IllegalStateException not in the right state
     */
    public void useCannon(PlayerData player, Float strength, List<Integer> batteriesID) throws IllegalStateException {
        if (internalState != CombatZoneInternalState.CANNONS) {
            throw new IllegalStateException("useCannon not allowed in this state");
        }
        // Use the energy tu use the cannon
        SpaceShip ship = player.getSpaceShip();
        for (Integer batteryID : batteriesID) {
            ship.useEnergy(batteryID);
        }
        // Update the cannon strength stats
        this.addStats(player, strength);
    }

    /**
     * Use the engine with a given strength
     * @param player PlayerData of the player using the engine
     * @param strength Strength of the engine to be used
     * @throws IllegalStateException not in the right state
     */
    public void useEngine(PlayerData player, Float strength, List<Integer> batteriesID) throws IllegalStateException {
        if (internalState != CombatZoneInternalState.ENGINES) {
            throw new IllegalStateException("useEngine not allowed in this state");
        }

        // Use the energy to power the engine
        SpaceShip ship = player.getSpaceShip();
        for (Integer batteryID : batteriesID) {
            ship.useEnergy(batteryID);
        }

        // Update the engine strength stats
        this.addStats(player, strength);
    }

    /**
     * Set the goods to discard for the player in order to serve the penalty
     * @param player the player that has to discard the goods
     * @param goodsToDiscard ArrayList of (in order) the good to discard and the storage id where to take it from
     */
    public void setGoodsToDiscard(PlayerData player, ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard) {
        if (internalState != CombatZoneInternalState.ENGINES) {
            throw new IllegalStateException("Selection of goods to discard not allowed in this state");
        }
        this.goodsToDiscard = goodsToDiscard;
    }

    /**
     * Entry: Add the default stats to the player
     */
    @Override
    public void entry() {
        SpaceShip spaceShip;
        for (PlayerData player : super.players) {
            spaceShip = player.getSpaceShip();
            this.addDefaultStats(CombatZoneInternalState.CREW, player, (float) spaceShip.getCrewNumber());
            if (minPlayerCrew == null || minPlayerCrew.getSpaceShip().getCrewNumber() > spaceShip.getCrewNumber()) {
                minPlayerCrew = player;
            }

            this.addDefaultStats(CombatZoneInternalState.ENGINES, player, (float) spaceShip.getSingleEnginesStrength());
            if (spaceShip.hasBrownAlien()) {
                this.addDefaultStats(CombatZoneInternalState.ENGINES, player, SpaceShip.getAlienStrength());
            }

            this.addDefaultStats(CombatZoneInternalState.CANNONS, player, spaceShip.getSingleCannonsStrength());
            if (spaceShip.hasPurpleAlien()) {
                this.addDefaultStats(CombatZoneInternalState.CANNONS, player, SpaceShip.getAlienStrength());
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
            case CombatZoneInternalState.CREW:
                if (card.getCardLevel() == 2) {
                    executeSubStateHits();
                } else {
                    executeSubStateFlightDays(minPlayerCrew);
                    transition();
                }
                break;
            case CombatZoneInternalState.ENGINES:
                minPlayerEngines = getMinPlayer();
                if (minPlayerEngines == null) {
                    throw new IllegalStateException("Not all player have set their engines");
                }

                if (card.getCardLevel() == 2) {
                    executeSubStateRemoveGoods(minPlayerEngines);
                } else {
                    if (crewLoss == null) {
                        throw new IllegalStateException("The min player have not set their crew loss");
                    }
                    executeSubStateRemoveCrew(minPlayerEngines);
                }
                transition();
                break;
            case CombatZoneInternalState.CANNONS:
                minPlayerCannons = getMinPlayer();
                if (minPlayerCannons == null) {
                    throw new IllegalStateException("Not all player have set their cannons");
                }

                if (card.getCardLevel() == 2) {
                    executeSubStateFlightDays(minPlayerCannons);
                    transition();
                } else {
                    executeSubStateHits();
                }
                break;
        }
    }
}