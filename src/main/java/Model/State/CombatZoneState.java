package Model.State;

import Model.Cards.Card;
import Model.Cards.CombatZone;
import Model.Cards.Hits.Hit;
import Model.Player.PlayerData;
import Model.SpaceShip.Battery;
import Model.SpaceShip.Component;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CombatZoneState extends State {
    private CombatZoneStatsType subState;
    private final Card card;
    private final ArrayList<Map<PlayerData, Float>> stats;
    private PlayerData minPlayerCannons;
    private int dice;
    private final boolean useEnergy;
    private int batteryID;
    List<List<Pair<Integer, Integer>>> fragments;
    private int fragmentChoice;
    private Pair<Component, Integer> protectionResult;
    private int hitIndex;
    private int fightIndex;

    public CombatZoneState(ArrayList<PlayerData> players, Card card) {
        super(players);
        this.card = card;
        this.stats = new ArrayList<>(3);
        this.subState = CombatZoneStatsType.CREW;
        this.useEnergy = false;
        this.fragmentChoice = 0;
        this.dice = 0;
        this.minPlayerCannons = null;
        this.batteryID = 0;
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
        stats.get(statsType.getIndex()).merge(player, value, Float::sum);
    }

    private void transition(CombatZoneStatsType statsType) {
        this.subState = statsType;
    }

    private void transitionHit() {
        hitIndex++;
        fightIndex = 0;
    }

    private List<List<Pair<Integer, Integer>>> destroyComponent(SpaceShip spaceShip, Component component) {
        spaceShip.destroyComponent(component.getRow(), component.getColumn());
        return spaceShip.getDisconnectedComponents();
    }

    private int comparePlayers(Map.Entry<PlayerData, Float> e1, Map.Entry<PlayerData, Float> e2) {
        int valueComparison = Float.compare(e1.getValue(), e2.getValue());
        if (valueComparison != 0) {
            return valueComparison;
        }
        int pos1 = super.getPlayerPosition(e1.getKey());
        int pos2 = super.getPlayerPosition(e2.getKey());
        return Integer.compare(pos1, pos2);
    }

    private void executeSubStateCrew() {
        int flightDays = ((CombatZone) card).getFlightDays();
        int statsIndex = CombatZoneStatsType.CREW.getIndex();

        stats.get(statsIndex).entrySet().stream().min(this::comparePlayers).ifPresent(entry -> {
            entry.getKey().addSteps(-flightDays);
        });

        transition(CombatZoneStatsType.ENGINES);
    }

    private void executeSubStateEngines(PlayerData player) {
        int crewLost = ((CombatZone) card).getLost();
        int statsIndex = CombatZoneStatsType.ENGINES.getIndex();

        stats.get(statsIndex).entrySet().stream().min(this::comparePlayers).ifPresent(entry -> {
            SpaceShip spaceShip = entry.getKey().getSpaceShip();
            spaceShip.addCrewMember(-crewLost);
            if (spaceShip.getCrewNumber() <= crewLost) {
                player.setGaveUp(true);
            }
        });

        transition(CombatZoneStatsType.CANNONS);
    }

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
                    Battery battery = spaceShip.getBattery(batteryID);
                    spaceShip.useEnergy(battery.getRow(), battery.getColumn());
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

    private void executeSubStateCannons(PlayerData player) {
        SpaceShip spaceShip = player.getSpaceShip();
        Hit hit = ((CombatZone) card).getFires().get(hitIndex);

        switch (fightIndex) {
            case 0:
                protectionResult = spaceShip.canProtect(dice, hit);
                fightIndex++;
                break;
            case 1:
                executeSubStateProtection(spaceShip);
                break;
            case 2:
                for (Pair<Integer, Integer> fragment : fragments.get(fragmentChoice)) {
                    spaceShip.destroyComponent(fragment.getValue0(), fragment.getValue1());
                }
                transitionHit();
                break;
        }
    }

    public void setFragmentChoice(int fragmentChoice) {
        this.fragmentChoice = fragmentChoice;
    }

    public void setBatteryID(int batteryID) {
        this.batteryID = batteryID;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    /**
     * Add stats to the player
     * @param statsType type of stats to add
     * @param player player to add to the stats
     * @param value value to add to the stats
     */
    public void addStats(CombatZoneStatsType statsType, PlayerData player, Float value) {
        addSingleCannonStats(statsType, player, value);
        if (statsType == CombatZoneStatsType.CANNONS) {
            boolean allPlayerPlayed = true;

            for (Pair<PlayerData, PlayerStatus> playerTemp : super.players) {
                if (playerTemp.getValue1() != PlayerStatus.PLAYED) {
                    allPlayerPlayed = false;
                    break;
                }
            }

            if (allPlayerPlayed) {
                stats.get(subState.getIndex()).entrySet().stream().min(this::comparePlayers).ifPresent(entry -> {
                    minPlayerCannons = entry.getKey();
                });
            }
        }
    }

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

    @Override
    public void execute(PlayerData player) {
        // TODO: differenziare i due tipi di carta per livello
        // TODO: sistemare utilizzo di player
        super.execute(player);
        switch (subState) {
            case CombatZoneStatsType.CREW:
                executeSubStateCrew();
                break;
            case CombatZoneStatsType.ENGINES:
                executeSubStateEngines(player);
                break;
            case CombatZoneStatsType.CANNONS:
                executeSubStateCannons(player);
                break;
        }
    }
}
