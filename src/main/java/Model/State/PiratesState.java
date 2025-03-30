package Model.State;

import Model.Cards.Hits.Hit;
import Model.Cards.Pirates;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: implements with FightHandler
public class PiratesState extends State {
    private final Pirates card;
    private final Map<PlayerData, Float> stats;
    Boolean piratesDefeat;
    Boolean acceptCredits;
    int execForPlayer;
    int fightIndex;
    int hitIndex;
    Integer dice;
    Boolean useEnergy;
    Integer fragmentChoice;
    Integer batteryID;
    Pair<Component, Integer> protectionResult;
    List<List<Pair<Integer, Integer>>> fragments;
    ArrayList<PlayerData> playersDefeated;

    /**
     * Constructor whit players and card
     * @param players List of players in the current order to play
     * @param card Pirates card associated with the state
     */
    public PiratesState(ArrayList<PlayerData> players, Pirates card) {
        super(players);
        this.card = card;
        this.stats = new HashMap<>();
        this.piratesDefeat = false;
        this.acceptCredits = false;
        this.fightIndex = 0;
        this.hitIndex = 0;
        this.execForPlayer = 0;
        this.dice = null;
        this.protectionResult = null;
        this.useEnergy = null;
        this.fragmentChoice = null;
        this.batteryID = null;
        this.playersDefeated = new ArrayList<>();
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
     * @param spaceShip SpaceShip of the player
     * @param component Component to destroy
     * @return List of disconnected components
     */
    private List<List<Pair<Integer, Integer>>> destroyComponent(SpaceShip spaceShip, Component component) {
        spaceShip.destroyComponent(component.getRow(), component.getColumn());
        return spaceShip.getDisconnectedComponents();
    }

    /**
     * Execute the protection
     * @param spaceShip SpaceShip of the player
     */
    private void executeProtection(SpaceShip spaceShip) {
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
     * Execute the hit
     * @param player PlayerData of the player to play
     * @throws IllegalStateException if dice, useEnergy, fragmentChoice not set
     */
    private void executeHit(PlayerData player) throws IllegalStateException {
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
                executeProtection(spaceShip);
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
     * @param fragmentChoice index of the fragment choice
     * @throws IllegalStateException if not in the correct state
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (execForPlayer != 2 || fightIndex != 2) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        this.fragmentChoice = fragmentChoice;
    }

    /**
     * Set the useEnergy and batteryID
     * @param useEnergy_ boolean value
     * @param batteryID_ ID of the battery
     * @throws IllegalStateException if not in the correct state
     * @throws IllegalArgumentException if useEnergy is true and batteryID is null
     */
    public void setUseEnergy(boolean useEnergy_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException {
        if (execForPlayer != 2 || fightIndex != 1) {
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
     * @param dice dice value
     */
    public void setDice(int dice) {
        if (execForPlayer != 2 || fightIndex != 0) {
            throw new IllegalStateException("setDice not allowed in this state");
        }
        this.dice = dice;
    }

    /**
     * Set if the player accepts the credits
     * @param acceptCredits Boolean value
     * @throws IllegalStateException if not in the correct state
     */
    public void setAcceptCredits(boolean acceptCredits) throws IllegalStateException {
        if (execForPlayer != 1) {
            throw new IllegalStateException("setAcceptCredits not allowed in this state");
        }
        this.acceptCredits = acceptCredits;
    }

    /**
     * Add stats to the player
     * @param player PlayerData
     * @param value Float value to add
     */
    public void addStats(PlayerData player, Float value) {
        this.stats.merge(player, value, Float::sum);
    }

    /**
     * Entry method, set the stats for the players
     */
    @Override
    public void entry() {
        PlayerData value0;
        for (Pair<PlayerData, PlayerStatus> player : super.players) {
            value0 = player.getValue0();
            this.addStats(value0, value0.getSpaceShip().getSingleCannonsStrength());
            if (value0.getSpaceShip().hasPurpleAlien()) {
                this.addStats(value0, SpaceShip.getAlienStrength());
            }
        }
    }

    /**
     * Execute.
     * @param player PlayerData of the player to play
     * @throws IllegalStateException if acceptCredits not set
     */
    @Override
    public void execute(PlayerData player) throws IllegalStateException {
        int cardValue = card.getCannonStrengthRequired();

        switch (execForPlayer) {
            case 0:
                if (stats.get(player) > cardValue) {
                    piratesDefeat = true;
                } else if (stats.get(player) < cardValue) {
                    piratesDefeat = false;
                } else {
                    piratesDefeat = null;
                }
                execForPlayer++;
                break;
            case 1:
                if (piratesDefeat == null) {
                    break;
                }

                if (piratesDefeat) {
                    if (acceptCredits == null) {
                        throw new IllegalStateException("acceptCredits not set");
                    }
                    if (acceptCredits) {
                        player.addCoins(card.getCredit());
                        player.addSteps(-card.getFlightDays());
                    }
                } else {
                    this.playersDefeated.add(player);
                }
                break;
            case 2:
                executeHit(player);
                break;
        }

        if (piratesDefeat != null && piratesDefeat && execForPlayer == 1) {
            super.setStatusPlayers(PlayerStatus.PLAYED);
        } else {
            super.execute(player);
        }

        if (super.haveAllPlayersPlayed()) {
            execForPlayer = 2;
            super.setStatusPlayers(PlayerStatus.WAITING);
        }
    }
}
