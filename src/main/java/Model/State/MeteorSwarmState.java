package Model.State;

import Model.Cards.Hits.Hit;
import Model.Cards.MeteorSwarm;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.List;

public class MeteorSwarmState extends State {
    private final MeteorSwarm card;
    private Integer dice;
    private int hitIndex;
    private int fightState;
    private Boolean protect;
    private List<List<Pair<Integer, Integer>>> fragments;
    private Pair<Component, Integer> protectionResult;
    private Integer fragmentChoice;
    private Integer batteryID;


    /**
     * Constructor
     * @param players players list
     * @param card card type
     */
    public MeteorSwarmState(ArrayList<PlayerData> players, MeteorSwarm card) {
        super(players);
        this.card = card;
        this.dice = null;
        this.fragmentChoice = null;
        this.protect = null;
        this.batteryID = null;
        this.hitIndex = -1;
        this.fightState = 0;
    }

    /**
     * Transition to the next hit
     */
    private void transitionHit() {
        hitIndex++;
        fightState = 0;
    }

    private void executeSubStateProtection(SpaceShip spaceShip) {
        Component component =  protectionResult.getValue0();
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
        }
    }

    private void executeSubStateFight(PlayerData player) throws IllegalStateException {
        SpaceShip spaceShip = player.getSpaceShip();

        switch (fightState) {
            case 0:
                if (hitIndex >= card.getMeteors().size()) {
                    throw new IndexOutOfBoundsException("Hit index out of bounds");
                }
                if (dice == null) {
                    throw new IllegalStateException("Dice not set");
                }
                Hit hit = card.getMeteors().get(hitIndex);
                protectionResult = spaceShip.canProtect(dice, hit);
                fightState++;
                break;
            case 1:
                if (protect == null) {
                    throw new IllegalStateException("Protect not set");
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
     * Get disconnectedComponents
     * @return List of (List of Pair(row, column)) of disconnected components
     * @throws IllegalStateException if method called is done in the wrong order
     */
    public List<List<Pair<Integer, Integer>>> getFragments() throws IllegalStateException {
        if (fightState != 3) {
            throw new IllegalStateException("getDisconnectedComponents done in the wrong order, current state: 3");
        }
        return fragments;
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
     */
    public void setDice(int dice) {
        if (fightState != 0) {
            throw new IllegalStateException("Dice not allowed in this state");
        }
        this.dice = dice;
    }

    /**
     * Get the number of hits
     * @return number of hits
     */
    public int getNumberOfHits() {
        return card.getMeteors().size();
    }

    /**
     * Execute: Check if player can protect, destroy components if necessary, choose which components to keep if necessary
     * @param player PlayerData of the player to play
     * @throws IndexOutOfBoundsException hitIndex out of bounds, toKeepComponents is out of bounds
     * @throws IllegalStateException Dice not set
     */
    @Override
    public void execute(PlayerData player) throws IndexOutOfBoundsException, IllegalStateException {
        this.executeSubStateFight(player);
    }
}
