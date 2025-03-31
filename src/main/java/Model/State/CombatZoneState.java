package Model.State;

import Model.Cards.CombatZone;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.handler.FightHandler;
import Model.State.interfaces.Fightable;
import Model.State.interfaces.RemovableCrew;
import Model.State.interfaces.UsableCannon;
import Model.State.interfaces.UsableEngine;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Map;

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


public class CombatZoneState extends State implements Fightable, RemovableCrew, UsableCannon, UsableEngine {
    private CombatZoneInternalState subState;
    private final CombatZone card;
    private final ArrayList<Map<PlayerData, Float>> stats;
    private PlayerData minPlayerEngines;
    private PlayerData minPlayerCannons;
    private Map<Integer, Integer> crewLoss;
    private final FightHandler fightHandler;

    /**
     * Constructor whit players and card
     * @param players List of players in the current order to play
     * @param card CombatZone card associated with the state
     */
    public CombatZoneState(ArrayList<PlayerData> players, CombatZone card) {
        super(players);
        this.card = card;
        this.stats = new ArrayList<>(3);
        this.subState = CombatZoneInternalState.CREW;
        this.minPlayerCannons = null;
        this.crewLoss = null;
        this.fightHandler = new FightHandler();
    }

    /**
     * Transition to the next state
     * @param statsType type of the next state
     */
    private void transition(CombatZoneInternalState statsType) {
        this.subState = statsType;
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
        boolean allPlayerPlayed = true;
        boolean isStateCrew = subState == CombatZoneInternalState.CREW;
        boolean isStateCannon = subState == CombatZoneInternalState.CANNONS;
        boolean isStateEngine = subState == CombatZoneInternalState.ENGINES;

        addDefaultStats(subState, player, value);
        if (!isStateCrew) {
            super.setStatusPlayer(player, PlayerStatus.PLAYED);
            for (Pair<PlayerData, PlayerStatus> playerTemp : super.players) {
                if (playerTemp.getValue1() != PlayerStatus.PLAYED) {
                    allPlayerPlayed = false;
                    break;
                }
            }
        }

        if (isStateEngine || isStateCannon) {
            if (allPlayerPlayed) {
                stats.get(subState.getIndex(card.getCardLevel())).entrySet().stream().min(this::comparePlayers).ifPresent(entry -> {
                    if (isStateCannon) {
                        minPlayerCannons = entry.getKey();
                    }
                    if (isStateEngine) {
                        minPlayerEngines = entry.getKey();
                    }
                });
                super.setStatusPlayers(PlayerStatus.WAITING);
            }
        }
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
    private void executeSubStateCrew() {
        int flightDays = card.getFlightDays();
        int statsIndex = CombatZoneInternalState.CREW.getIndex(card.getCardLevel());

        stats.get(statsIndex).entrySet().stream().min(this::comparePlayers).ifPresent(entry ->
                entry.getKey().addSteps(-flightDays)
        );

        transition(CombatZoneInternalState.ENGINES);
    }

    /**
     * Execute the subState engines
     * @param player player to execute
     */
    private void executeSubStateEngines(PlayerData player) throws IllegalArgumentException {
        int crewLost = card.getLost();

        SpaceShip spaceShip = minPlayerEngines.getSpaceShip();
        if (spaceShip.getCrewNumber() <= crewLost) {
            player.setGaveUp(true);
        } else {
            for (Map.Entry<Integer, Integer> cabinID : crewLoss.entrySet()) {
                spaceShip.removeCrewMember(cabinID.getKey(), cabinID.getValue());
            }
        }

        transition(CombatZoneInternalState.CANNONS);
        fightHandler.initialize(0);
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice fragment choice
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (subState != CombatZoneInternalState.CANNONS) {
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
        if (subState != CombatZoneInternalState.CANNONS) {
            throw new IllegalStateException("Battery ID not allowed in this state");
        }
        fightHandler.setProtect(protect_, batteryID_);
    }

    /**
     * Set the dice
     * @param dice dice
     */
    public void setDice(int dice) throws IllegalStateException{
        if (subState != CombatZoneInternalState.CANNONS) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        fightHandler.setDice(dice);
    }

    /**
     * Set the cabins ID
     * @param cabinsID Map of cabins ID and number of crew removed for cabins
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setCrewLoss(Map<Integer, Integer> cabinsID) throws IllegalStateException {
        if (subState != CombatZoneInternalState.ENGINES) {
            throw new IllegalStateException("setCabinsID not allowed in this state");
        }

        int crewRemoved = 0;
        for (Map.Entry<Integer, Integer> cabinID : cabinsID.entrySet()) {
            crewRemoved += cabinID.getValue();
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
     * @throws IllegalStateException not in the right state
     */
    public void useCannon(PlayerData player, Float strength) throws IllegalStateException {
        if (subState != CombatZoneInternalState.CANNONS) {
            throw new IllegalStateException("useCannon not allowed in this state");
        }
        this.addStats(player, strength);
    }

    /**
     * Use the engine with a given strength
     * @param player PlayerData of the player using the engine
     * @param strength Strength of the engine to be used
     * @throws IllegalStateException not in the right state
     */
    public void useEngine(PlayerData player, Float strength) throws IllegalStateException {
        if (subState != CombatZoneInternalState.ENGINES) {
            throw new IllegalStateException("useEngine not allowed in this state");
        }
        this.addStats(player, strength);
    }

    /**
     * Entry: Add the default stats to the player
     */
    @Override
    public void entry() {
        PlayerData value0;
        for (Pair<PlayerData, PlayerStatus> player : super.players) {
            value0 = player.getValue0();
            this.addDefaultStats(CombatZoneInternalState.CREW, value0, (float) value0.getSpaceShip().getCrewNumber());

            this.addDefaultStats(CombatZoneInternalState.ENGINES, value0, (float) value0.getSpaceShip().getSingleEnginesStrength());
            if (value0.getSpaceShip().hasBrownAlien()) {
                this.addDefaultStats(CombatZoneInternalState.ENGINES, value0, SpaceShip.getAlienStrength());
            }

            this.addDefaultStats(CombatZoneInternalState.CANNONS, value0, value0.getSpaceShip().getSingleCannonsStrength());
            if (value0.getSpaceShip().hasPurpleAlien()) {
                this.addDefaultStats(CombatZoneInternalState.CANNONS, value0, SpaceShip.getAlienStrength());
            }
        }
    }

    /**
     * Execute one state each of teh cards
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        // TODO: sistemo subState
        switch (subState) {
            case CombatZoneInternalState.CREW:
                executeSubStateCrew();
                break;
            case CombatZoneInternalState.ENGINES:
                if (minPlayerEngines == null) {
                    throw new IllegalStateException("Not all player have set their engines");
                }
                if (crewLoss == null) {
                    throw new IllegalStateException("The min player have not set their crew loss");
                }

                executeSubStateEngines(minPlayerEngines);
                break;
            case CombatZoneInternalState.CANNONS:
                if (minPlayerCannons == null) {
                    throw new IllegalStateException("Not all player have set their cannons");
                }
                int currentHitIndex = fightHandler.getHitIndex();
                if (currentHitIndex >= card.getFires().size()) {
                    throw new IndexOutOfBoundsException("Hit index out of bounds");
                }

                fightHandler.executeFight(minPlayerCannons.getSpaceShip(), () -> card.getFires().get(currentHitIndex));
                break;
        }
    }
}