package Model.State;

import Model.Cards.Hits.Hit;
import Model.SpaceShip.Component;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.function.Supplier;

enum FightHandlerInternalState {
    CAN_PROTECT,
    PROTECTION,
    DESTROY_FRAGMENT
}

public class FightHandler {
    private Integer dice;
    private Boolean protect;
    private Integer batteryID;
    private Integer fragmentChoice;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> fragments;
    private Pair<Component, Integer> protectionResult;
    private int hitIndex;
    private FightHandlerInternalState internalState;

    /**
     * Constructor
     */
    public FightHandler() {
        this.dice = null;
        this.protect = null;
        this.batteryID = null;
        this.fragmentChoice = null;
        this.hitIndex = 0;
        this.internalState = FightHandlerInternalState.CAN_PROTECT;
    }

    public void setHitIndex(int hitIndex) {
        this.hitIndex = hitIndex;
    }

    public int getDice() {
        return dice;
    }

    public Boolean getProtect() {
        return protect;
    }

    public Integer getBatteryID() {
        return batteryID;
    }

    public Integer getFragmentChoice() {
        return fragmentChoice;
    }

    public FightHandlerInternalState getInternalState() {
        return internalState;
    }

    public void setProtectionResult(Pair<Component, Integer> protectionResult) {
        this.protectionResult = protectionResult;
    }

    public void setInternalState(FightHandlerInternalState internalState) {
        this.internalState = internalState;
    }

    public void setFragments(ArrayList<ArrayList<Pair<Integer, Integer>>> fragments) {
        this.fragments = fragments;
    }

    /**
     * Initialize or reset the handler
     * @param startIndex The hit index to start with
     */
    public void initialize(int startIndex) {
        this.hitIndex = startIndex;
        this.internalState = FightHandlerInternalState.CAN_PROTECT;
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
        internalState = FightHandlerInternalState.CAN_PROTECT;
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
        if (internalState != FightHandlerInternalState.DESTROY_FRAGMENT) {
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
        if (internalState != FightHandlerInternalState.PROTECTION) {
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
        if (internalState != FightHandlerInternalState.CAN_PROTECT) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        this.dice = dice;
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
    public ArrayList<ArrayList<Pair<Integer, Integer>>> getFragments() {
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
                    internalState = FightHandlerInternalState.DESTROY_FRAGMENT;
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
     * @return If the execution is complete for the current hit
     */
    public boolean executeFight(SpaceShip spaceShip, Supplier<Hit> hitSupplier) throws IndexOutOfBoundsException, IllegalStateException {
        switch (internalState) {
            case CAN_PROTECT:
                if (dice == null) {
                    throw new IllegalStateException("Dice not set");
                }
                Hit hit = hitSupplier.get();
                protectionResult = spaceShip.canProtect(dice, hit);
                internalState = FightHandlerInternalState.PROTECTION;
                break;
            case PROTECTION:
                if (protect == null) {
                    throw new IllegalStateException("Protect not set");
                }
                executeProtection(spaceShip);
                break;
            case DESTROY_FRAGMENT:
                if (fragmentChoice == null) {
                    throw new IllegalStateException("FragmentChoice not set");
                }
                for (int i = 0; i < fragments.size(); i++) {
                    if (i != fragmentChoice) {
                        ArrayList<Pair<Integer, Integer>> fragment = fragments.get(i);
                        for (Pair<Integer, Integer> component : fragment) {
                            spaceShip.destroyComponent(component.getValue0(), component.getValue1());
                        }
                    }
                }
                transitionHit();
                return true;
        }
        return false;
    }
}