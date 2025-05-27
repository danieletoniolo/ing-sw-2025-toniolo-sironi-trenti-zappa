package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.*;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class FightHandlerSubState extends State {
    private Integer dice;
    private Boolean protect;
    private Integer batteryID;
    private Integer fragmentChoice;
    private List<List<Pair<Integer, Integer>>> fragments;
    private Pair<Component, Integer> protectionResult;
    private int hitIndex;
    private FightHandlerInternalState internalState;

    /**
     * Enum to represent the internal state of the fight handler substate.
     */
    private enum FightHandlerInternalState {
        CAN_PROTECT,
        PROTECTION,
        DESTROY_FRAGMENT
    }

    /**
     * Constructor
     */
    public FightHandlerSubState(Board board, EventCallback eventCallback, StateTransitionHandler transitionHandler) {
        super(board, eventCallback, transitionHandler);
        this.dice = null;
        this.protect = null;
        this.batteryID = null;
        this.fragmentChoice = null;
        this.hitIndex = 0;
        this.internalState = FightHandlerInternalState.CAN_PROTECT;
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

        NextHit nextHitEvent = new NextHit();
        eventCallback.trigger(nextHitEvent);
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
        if (fragmentChoice < 0 || fragmentChoice >= fragments.size()) {
            throw new IllegalArgumentException("Fragment choice is out of bounds");
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
    public List<List<Pair<Integer, Integer>>> getFragments() {
        return fragments;
    }

    /**
     * Execute protection logic
     * @param player Player that is playing
     */
    public void executeProtection(PlayerData player) {
        SpaceShip spaceShip = player.getSpaceShip();
        Component component = protectionResult.getValue0();
        int protectionType = protectionResult.getValue1();

        if (protectionType == 0 || protectionType == -1) {
            if (protect) {
                spaceShip.useEnergy(batteryID);

                ShieldUsed useShield = new ShieldUsed(player.getUsername(), batteryID);
                eventCallback.trigger(useShield);

                transitionHit();
            } else {
                spaceShip.destroyComponent(component.getRow(), component.getColumn());

                ArrayList<Pair<Integer, Integer>> destroyedComponents = new ArrayList<>();
                destroyedComponents.add(new Pair<>(component.getRow(), component.getColumn()));
                ComponentDestroyed destroyComponentsEvent = new ComponentDestroyed(player.getUsername(), destroyedComponents);
                eventCallback.trigger(destroyComponentsEvent);

                fragments = spaceShip.getDisconnectedComponents();
                if (fragments.size() > 1) {
                    internalState = FightHandlerInternalState.DESTROY_FRAGMENT;

                    Fragments fragmentChoiceEvent = new Fragments(player.getUsername(), fragments);
                    eventCallback.trigger(fragmentChoiceEvent);
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
     * @param player The player who is fighting
     * @param hitSupplier Supplier that provides the current Hit
     * @throws IndexOutOfBoundsException If hitIndex is out of bounds
     * @throws IllegalStateException If required state variables are not set
     * @return If the execution is complete for the current hit
     */
    public boolean executeFight(PlayerData player, Supplier<Hit> hitSupplier) throws IndexOutOfBoundsException, IllegalStateException {
        SpaceShip spaceShip = player.getSpaceShip();
        switch (internalState) {
            case CAN_PROTECT:
                if (dice == null) {
                    throw new IllegalStateException("Dice not set");
                }
                Hit hit = hitSupplier.get();
                protectionResult = spaceShip.canProtect(dice, hit);
                internalState = FightHandlerInternalState.PROTECTION;

                CanProtect canProtectEvent = new CanProtect(player.getUsername(), new Pair<>(protectionResult.getValue0().getID(), protectionResult.getValue1()));
                eventCallback.trigger(canProtectEvent);
                break;
            case PROTECTION:
                if (protect == null) {
                    throw new IllegalStateException("Protect not set");
                }
                executeProtection(player);
                break;
            case DESTROY_FRAGMENT:
                if (fragmentChoice == null) {
                    throw new IllegalStateException("FragmentChoice not set");
                }
                ArrayList<Pair<Integer, Integer>> destroyedComponents = new ArrayList<>();
                for (int i = 0; i < fragments.size(); i++) {
                    if (i != fragmentChoice) {
                        List<Pair<Integer, Integer>> fragment = fragments.get(i);
                        for (Pair<Integer, Integer> component : fragment) {
                            spaceShip.destroyComponent(component.getValue0(), component.getValue1());
                            destroyedComponents.add(new Pair<>(component.getValue0(), component.getValue1()));
                        }
                    }
                }

                ComponentDestroyed destroyComponentsEvent = new ComponentDestroyed(player.getUsername(), destroyedComponents);
                eventCallback.trigger(destroyComponentsEvent);
                transitionHit();
                return true;
        }
        return false;
    }
}