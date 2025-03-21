package Model.State;

import Model.Cards.CombatZone;
import Model.Cards.Hits.Hit;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CombatZoneState extends State {
    private CombatZoneStatsType subState;
    private final CombatZone card;
    private final ArrayList<Map<PlayerData, Float>> stats;
    private PlayerData minPlayerEngines;
    private PlayerData minPlayerCannons;
    private Integer dice;
    private Boolean useEnergy;
    private Integer batteryID;
    List<List<Pair<Integer, Integer>>> fragments;
    private Integer fragmentChoice;
    private Pair<Component, Integer> protectionResult;
    private int hitIndex;
    private int fightIndex;

    public CombatZoneState(ArrayList<PlayerData> players, CombatZone card) {
        super(players);
        this.card = card;
        this.stats = new ArrayList<>(3);
        this.subState = CombatZoneStatsType.CREW;
        this.useEnergy = null;
        this.fragmentChoice = null;
        this.dice = null;
        this.minPlayerCannons = null;
        this.batteryID = null;
        this.hitIndex = 0;
        this.fightIndex = 0;
    }

    /**
     * Add stats to the player
     * @param statsType type of stats to add
     * @param player player to add to the stats
     * @param value value to add to the stats
     */
    private void addSingleCannonStats(CombatZoneStatsType statsType, PlayerData player, Float value) {
        stats.get(statsType.getIndex(card.getCardLevel())).merge(player, value, Float::sum);
    }

    /**
     * Transition to the next state
     * @param statsType type of the next state
     */
    private void transition(CombatZoneStatsType statsType) {
        this.subState = statsType;
    }

    /**
     * Transition to the next hit
     */
    private void transitionHit() {
        hitIndex++;
        fightIndex = 0;
    }

    /**
     * Destroy a component
     * @param spaceShip spaceShip to destroy the component
     * @param component component to destroy
     * @return List of disconnected components
     */
    private List<List<Pair<Integer, Integer>>> destroyComponent(SpaceShip spaceShip, Component component) {
        spaceShip.destroyComponent(component.getRow(), component.getColumn());
        return spaceShip.getDisconnectedComponents();
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
        int statsIndex = CombatZoneStatsType.CREW.getIndex(card.getCardLevel());

        stats.get(statsIndex).entrySet().stream().min(this::comparePlayers).ifPresent(entry ->
            entry.getKey().addSteps(-flightDays)
        );

        transition(CombatZoneStatsType.ENGINES);
    }

    /**
     * Execute the subState engines
     * @param player player to execute
     */
    private void executeSubStateEngines(PlayerData player) {
        int crewLost = card.getLost();
        int statsIndex = CombatZoneStatsType.ENGINES.getIndex(card.getCardLevel());

        stats.get(statsIndex).entrySet().stream().min(this::comparePlayers).ifPresent(entry -> {
            SpaceShip spaceShip = entry.getKey().getSpaceShip();
            spaceShip.addCrewMember(-crewLost);
            if (spaceShip.getCrewNumber() <= crewLost) {
                player.setGaveUp(true);
            }
        });

        transition(CombatZoneStatsType.CANNONS);
    }

    /**
     * Execute the sub state protection
     * @param spaceShip spaceShip to execute
     */
    private void executeSubStateProtection(SpaceShip spaceShip) {
        Component component = protectionResult.getValue0();
        int protectionType = protectionResult.getValue1();

        switch (protectionType) {
            case -1:
                fragments = destroyComponent(spaceShip, component);
                if (fragments.size() > 1) {
                    fightIndex++;
                } else {
                    transitionHit();
                }
                break;
            case 0:
                if (useEnergy) {
                    spaceShip.useEnergy(batteryID);
                    transitionHit();
                } else {
                    fragments = destroyComponent(spaceShip, component);
                    if (fragments.size() > 1) {
                        fightIndex++;
                    } else {
                        transitionHit();
                    }
                }
                break;
            case 1:
                break;
        }
    }

    /**
     * Execute the subState cannons
     * @param player player to execute
     * @throws IllegalStateException if dice not set, useEnergy not set, fragmentChoice not set
     */
    private void executeSubStateCannons(PlayerData player) throws IllegalStateException {
        SpaceShip spaceShip = player.getSpaceShip();
        Hit hit = card.getFires().get(hitIndex);

        switch (fightIndex) {
            case 0:
                if (dice == null) {
                    throw new IllegalStateException("Dice not set");
                }
                protectionResult = spaceShip.canProtect(dice, hit);
                fightIndex++;
                break;
            case 1:
                if (useEnergy == null) {
                    throw new IllegalStateException("UseEnergy not set");
                }
                executeSubStateProtection(spaceShip);
                break;
            case 2:
                if (fragmentChoice == null) {
                    throw new IllegalStateException("FragmentChoice not set");
                }
                for (Pair<Integer, Integer> fragment : fragments.get(fragmentChoice)) {
                    spaceShip.destroyComponent(fragment.getValue0(), fragment.getValue1());
                }
                transitionHit();
                break;
        }
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice fragment choice
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (subState != CombatZoneStatsType.CANNONS || fightIndex != 2) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        this.fragmentChoice = fragmentChoice;
    }

    /**
     * Set the use energy
     * @param useEnergy_ use energy
     * @param batteryID_ battery ID
     * @throws IllegalStateException if not in the right state in order to do the action
     * @throws IllegalArgumentException if batteryID_ is null and useEnergy_ is true
     */
    public void setUseEnergy(boolean useEnergy_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException {
        if (subState != CombatZoneStatsType.CANNONS || fightIndex != 1) {
            throw new IllegalStateException("Battery ID not allowed in this state");
        }
        this.useEnergy = useEnergy_;
        if (useEnergy_ && batteryID_ == null) {
            throw new IllegalArgumentException("If you set useEnergy to true, you have to set the batteryID");
        }
        this.batteryID = batteryID_;
    }

    /**
     * Set the dice
     * @param dice dice
     */
    public void setDice(int dice) {
        if (subState != CombatZoneStatsType.CANNONS || fightIndex != 0) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        this.dice = dice;
    }

    /**
     * Add stats to the player, and if all the player have set their stats, find the player with the lowest stats
     * @param statsType type of stats to add
     * @param player player to add to the stats
     * @param value value to add to the stats
     */
    public void addStats(CombatZoneStatsType statsType, PlayerData player, Float value) {
        boolean allPlayerPlayed = true;
        boolean isStateCrew = statsType == CombatZoneStatsType.CREW;
        boolean isStateCannon = statsType == CombatZoneStatsType.CANNONS;
        boolean isStateEngine = statsType == CombatZoneStatsType.ENGINES;

        addSingleCannonStats(statsType, player, value);
        if (!isStateCrew) {
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
            }
        }
    }

    /**
     * Entry: Add the default stats to the player
     */
    @Override
    public void entry() {
        PlayerData value0;
        for (Pair<PlayerData, PlayerStatus> player : super.players) {
            value0 = player.getValue0();
            this.addSingleCannonStats(CombatZoneStatsType.CREW, value0, (float) value0.getSpaceShip().getCrewNumber());

            this.addSingleCannonStats(CombatZoneStatsType.ENGINES, value0, (float) value0.getSpaceShip().getSingleEnginesStrength());
            if (value0.getSpaceShip().getBrownAlien()) {
                this.addSingleCannonStats(CombatZoneStatsType.ENGINES, value0, SpaceShip.getAlienStrength());
            }

            this.addSingleCannonStats(CombatZoneStatsType.CANNONS, value0, value0.getSpaceShip().getSingleCannonsStrength());
            if (value0.getSpaceShip().getPurpleAlien()) {
                this.addSingleCannonStats(CombatZoneStatsType.CANNONS, value0, SpaceShip.getAlienStrength());
            }
        }
    }

    /**
     * Execute one state each of teh cards
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        // TODO: sistemare utilizzo di player
        super.execute(player);
        switch (subState) {
            case CombatZoneStatsType.CREW:
                executeSubStateCrew();
                break;
            case CombatZoneStatsType.ENGINES:
                if (minPlayerEngines == null) {
                    throw new IllegalStateException("Not all player have set their engines");
                }
                executeSubStateEngines(player);
                break;
            case CombatZoneStatsType.CANNONS:
                if (minPlayerCannons == null) {
                    throw new IllegalStateException("Not all player have set their cannons");
                }
                executeSubStateCannons(player);
                break;
        }
    }
}
