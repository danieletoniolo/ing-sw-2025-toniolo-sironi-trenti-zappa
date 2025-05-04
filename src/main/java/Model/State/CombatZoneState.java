package Model.State;

import Model.Cards.CombatZone;
import Model.Game.Board.Board;
import Model.Good.Good;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.*;
import org.javatuples.Pair;

import java.util.*;

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
        for (int i = 0; i < 3; i++) {
            stats.add(new HashMap<>());
        }
        this.internalState = CombatZoneInternalState.CREW;
        this.minPlayerCannons = null;
        this.minPlayerEngines = null;
        this.minPlayerCrew = null;
        this.crewLoss = null;
        this.goodsToDiscard = null;
        this.fightHandler = new FightHandler();
    }

    public void setInternalState(CombatZoneInternalState internalState) {
        this.internalState = internalState;
    }

    public CombatZoneInternalState getInternalState() {
        return internalState;
    }

    public CombatZone getCard() {
        return card;
    }

    public ArrayList<Map<PlayerData, Float>> getStats() {
        return stats;
    }

    public PlayerData getMinPlayerEngines() {
        return minPlayerEngines;
    }

    public PlayerData getMinPlayerCannons() {
        return minPlayerCannons;
    }

    public PlayerData getMinPlayerCrew() {
        return minPlayerCrew;
    }

    public ArrayList<Pair<Integer, Integer>> getCrewLoss() {
        return crewLoss;
    }

    public ArrayList<Pair<ArrayList<Good>, Integer>> getGoodsToDiscard() {
        return goodsToDiscard;
    }

    public FightHandler getFightHandler() {
        return fightHandler;
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
     * Use the batteries to power the engines
     * @param player PlayerData of the player using the engine
     * @param batteriesID List of Integers representing the batteryID from which we use the energy to use the engine
     */
    private void useBatteries(PlayerData player, List<Integer> batteriesID) {
        SpaceShip ship = player.getSpaceShip();
        for (Integer batteryID : batteriesID) {
            ship.useEnergy(batteryID);
        }
    }

    /**
     * Execute the subState crew
     */
    private void executeSubStateFlightDays(PlayerData player) {
        int flightDays = card.getFlightDays();
        board.addSteps(player, -flightDays);
        playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.WAITING);
    }

    /**
     * Execute the subState engines
     * @param player player to execute
     */
    private void executeSubStateRemoveCrew(PlayerData player) throws IllegalArgumentException {
        int crewLost = card.getLost();

        SpaceShip spaceShip = player.getSpaceShip();
        if (spaceShip.getCrewNumber() <= crewLost) {
            player.setGaveUp(true);
        } else {
            for (Pair<Integer, Integer> cabin : crewLoss) {
                spaceShip.removeCrewMember(cabin.getValue0(), cabin.getValue1());
            }
            playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.WAITING);
        }
    }

    private void executeSubStateHits(PlayerData player) throws IndexOutOfBoundsException {
        int currentHitIndex = fightHandler.getHitIndex();
        if (currentHitIndex >= card.getFires().size()) {
            throw new IndexOutOfBoundsException("Hit index out of bounds");
        }

        boolean complete = fightHandler.executeFight(player.getSpaceShip(), () -> card.getFires().get(currentHitIndex));
        if (complete && currentHitIndex == card.getFires().size() - 1) {
            super.execute(player);
            transition();
        }
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
                ship.removeCrewMember(pair.getValue0(), pair.getValue1());
            }
        }

        // Reset the goods to discard
        this.goodsToDiscard = null;
        // Set the player as played
        playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.WAITING);
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice fragment choice
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if ((internalState != CombatZoneInternalState.CANNONS && card.getCardLevel() != 2) ||
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
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
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
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
                (internalState != CombatZoneInternalState.CREW && card.getCardLevel() == 2)) {
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
        useBatteries(player, batteriesID);

        // Update the cannon strength stats
        this.addStats(player, strength);

        if (stats.get(internalState.getIndex(card.getCardLevel())).get(player) > stats.get(internalState.getIndex(card.getCardLevel())).get(minPlayerCannons)) {
            playersStatus.replace(minPlayerCannons.getColor(), PlayerStatus.WAITING);
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYING);
            minPlayerCannons = player;
        }
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
        useBatteries(player, batteriesID);

        // Update the engine strength stats
        this.addStats(player, strength);

        if (stats.get(internalState.getIndex(card.getCardLevel())).get(player) > stats.get(internalState.getIndex(card.getCardLevel())).get(minPlayerEngines)) {
            playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.WAITING);
            playersStatus.replace(player.getColor(), PlayerStatus.PLAYING);
            minPlayerEngines = player;
        }
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
            if (minPlayerEngines == null || minPlayerEngines.getSpaceShip().getSingleEnginesStrength() > spaceShip.getSingleEnginesStrength()) {
                minPlayerEngines = player;
            }

            this.addDefaultStats(CombatZoneInternalState.CANNONS, player, spaceShip.getSingleCannonsStrength());
            if (spaceShip.hasPurpleAlien()) {
                this.addDefaultStats(CombatZoneInternalState.CANNONS, player, SpaceShip.getAlienStrength());
            }
            if (minPlayerCannons == null || minPlayerCannons.getSpaceShip().getSingleCannonsStrength() > spaceShip.getSingleCannonsStrength()) {
                minPlayerCannons = player;
            }
        }
        if (card.getCardLevel() == 2) {
            playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
        } else {
            playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
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
                    executeSubStateHits(minPlayerCrew);
                } else {
                    executeSubStateFlightDays(minPlayerCrew);
                    playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
                    transition();
                }
                break;
            case CombatZoneInternalState.ENGINES:
                if (card.getCardLevel() == 2) {
                    int crewFulfillment = minPlayerEngines.getSpaceShip().getGoods().size() - card.getLost();
                    if (crewFulfillment < 0 && minPlayerEngines.getSpaceShip().getCrewNumber() < Math.abs(crewFulfillment)) {
                        minPlayerEngines.setGaveUp(true);
                        super.board.refreshInGamePlayers();
                        this.players = super.board.getInGamePlayers();
                    } else {
                        executeSubStateRemoveGoods(minPlayerEngines);
                    }
                    playersStatus.replace(minPlayerCrew.getColor(), PlayerStatus.PLAYING);
                } else {
                    if (crewLoss == null) {
                        throw new IllegalStateException("The min player have not set their crew loss");
                    }
                    executeSubStateRemoveCrew(minPlayerEngines);
                    playersStatus.replace(minPlayerCannons.getColor(), PlayerStatus.PLAYING);
                }
                transition();
                break;
            case CombatZoneInternalState.CANNONS:
                if (card.getCardLevel() == 2) {
                    executeSubStateFlightDays(minPlayerCannons);
                    playersStatus.replace(minPlayerEngines.getColor(), PlayerStatus.PLAYING);
                    transition();
                } else {
                    executeSubStateHits(minPlayerCannons);
                }
                break;
        }
    }
}