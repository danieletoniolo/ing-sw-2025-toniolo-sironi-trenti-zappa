package Model.State;

import Model.Cards.MeteorSwarm;
import Model.Player.PlayerData;
import Model.State.Handler.FightHandler;
import Model.State.Interface.Fightable;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.List;

public class MeteorSwarmState extends State implements Fightable {
    private final MeteorSwarm card;
    private final FightHandler fightHandler;

    /**
     * Constructor
     * @param players players list
     * @param card card type
     */
    public MeteorSwarmState(ArrayList<PlayerData> players, MeteorSwarm card) {
        super(players);
        this.card = card;
        this.fightHandler = new FightHandler();
    }

    /**
     * Get disconnectedComponents
     * @return List of (List of Pair(row, column)) of disconnected components
     * @throws IllegalStateException if method called is done in the wrong order
     */
    public List<List<Pair<Integer, Integer>>> getFragments() throws IllegalStateException {
        if (fightHandler.getFightState() != 2) {
            throw new IllegalStateException("getDisconnectedComponents done in the wrong order, current state: " + fightHandler.getFightState());
        }
        return fightHandler.getFragments();
    }

    /**
     * Set the fragment choice
     * @param fragmentChoice fragment choice
     * @throws IllegalStateException if not in the right state in order to do the action
     */
    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
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
        fightHandler.setProtect(protect_, batteryID_);
    }

    /**
     * Set dice
     * @param dice dice value
     */
    public void setDice(int dice) {
        fightHandler.setDice(dice);
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
        int currentHitIndex = fightHandler.getHitIndex();
        if (currentHitIndex >= card.getMeteors().size()) {
            throw new IndexOutOfBoundsException("Hit index out of bounds");
        }

        fightHandler.executeFight(player.getSpaceShip(), () -> card.getMeteors().get(currentHitIndex));
    }
}