package Model.State.Handler;

import Model.Cards.Hits.Hit;
import Model.SpaceShip.Component;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.List;
import java.util.function.Supplier;

public class FightHandler {
    private Integer dice;
    private Boolean protect;
    private Integer batteryID;
    private Integer fragmentChoice;
    private List<List<Pair<Integer, Integer>>> fragments;
    private Pair<Component, Integer> protectionResult;
    private int hitIndex;
    private int fightState;

    /**
     * Constructor
     */
    public FightHandler() {
        this.dice = null;
        this.protect = null;
        this.batteryID = null;
        this.fragmentChoice = null;
        this.hitIndex = 0;
        this.fightState = 0;
    }

    /**
     * Initialize or reset the handler
     * @param startIndex The hit index to start with
     */
    public void initialize(int startIndex) {
        this.hitIndex = startIndex;
        this.fightState = 0;
        this.dice = null;
        this.protect = null;
        this.batteryID = null;
        this.fragmentChoice = null;
    }

    /**
     * Reset the handler state for a new hit
     */
    public void transitionHit() {
        hitIndex++;
        fightState = 0;
        dice = null;
        protect = null;
        batteryID = null;
        fragmentChoice = null;
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice fragment choice
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (fightState != 2) {
            throw new IllegalStateException("Fragment choice not allowed in this state");
        }
        this.fragmentChoice = fragmentChoice;
    }

    /**
     * Set the use energy
     * @param protect_ use energy
     * @param batteryID_ battery ID
     * @throws IllegalStateException if not in the right state in order to do the action
     * @throws IllegalArgumentException if batteryID_ is null and protect_ is true
     */
    public void setProtect(boolean protect_, Integer batteryID_) throws IllegalStateException, IllegalArgumentException {
        if (fightState != 1) {
            throw new IllegalStateException("Battery ID not allowed in this state");
        }
        this.protect = protect_;
        if (protect_ && batteryID_ == null) {
            throw new IllegalArgumentException("If you set protect to true, you have to set the batteryID");
        }
        this.batteryID = batteryID_;
    }

    /**
     * Set dice
     * @param dice dice value
     * @throws IllegalStateException if not in the right state to set dice
     */
    public void setDice(int dice) throws IllegalStateException {
        if (fightState != 0) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        this.dice = dice;
    }

    /**
     * Get the current state of the fight
     * @return current fight state
     */
    public int getFightState() {
        return fightState;
    }

    /**
     * Get the current hit index
     * @return current hit index
     */
    public int getHitIndex() {
        return hitIndex;
    }

    /**
     * Get the fragments
     * @return fragments
     */
    public List<List<Pair<Integer, Integer>>> getFragments() {
        return fragments;
    }

    /**
     * Execute protection logic
     * @param spaceShip The spaceship to protect
     */
    public void executeProtection(SpaceShip spaceShip) {
        Component component = protectionResult.getValue0();
        int protectionType = protectionResult.getValue1();

        if (protectionType == 0 || protectionType == -1) {
            if (protect) {
                spaceShip.useEnergy(batteryID);
                transitionHit();
            } else {
                spaceShip.destroyComponent(component.getRow(), component.getColumn());
                fragments = spaceShip.getDisconnectedComponents();
                if (fragments.size() > 1) {
                    fightState++;
                } else {
                    transitionHit();
                }
            }
        } else {
            transitionHit();
        }
    }

    /**
     * Execute fight state machine
     * @param spaceShip The spaceship to fight with
     * @param hitSupplier Supplier that provides the current Hit
     * @throws IndexOutOfBoundsException If hitIndex is out of bounds
     * @throws IllegalStateException If required state variables are not set
     */
    public void executeFight(SpaceShip spaceShip, Supplier<Hit> hitSupplier) throws IndexOutOfBoundsException, IllegalStateException {
        switch (fightState) {
            case 0:
                if (dice == null) {
                    throw new IllegalStateException("Dice not set");
                }
                Hit hit = hitSupplier.get();
                protectionResult = spaceShip.canProtect(dice, hit);
                fightState++;
                break;
            case 1:
                if (protect == null) {
                    throw new IllegalStateException("Protect not set");
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
}