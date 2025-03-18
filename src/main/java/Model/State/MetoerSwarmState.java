package Model.State;

import Model.Cards.Hits.Hit;
import Model.Cards.MeteorSwarm;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.List;

public class MetoerSwarmState extends State {
    private final MeteorSwarm card;
    private int dice;
    private int hitIndex;
    private int state;
    private int canProtect;
    private Boolean protection;
    private List<List<Pair<Integer, Integer>>> disconnectedComponents;
    private int toKeepComponents;
    private Pair<Component, Integer> var;
    private int ID;

    /**
     * Constructor
     * @param players players list
     * @param card card type
     */
    public MetoerSwarmState(ArrayList<PlayerData> players, MeteorSwarm card) {
        super(players);
        this.card = card;
        hitIndex = -1;
        state = 0;
    }

    /**
     * Set dice
     * @param dice dice value
     */
    public void setDice(int dice) {
        if (state != 0) {
            throw new IllegalStateException("setDice done in the wrong order, current state: 0");
        }
        this.dice = dice;
        state = 1;
        hitIndex++;
    }

    /**
     * Get canProtect
     * @return canProtect = result of the method canProtect
     */
    public int getCanProtect() throws IllegalStateException{
        if (state != 2) {
            throw new IllegalStateException("getCanProtect done in the wrong order, current state: 2");
        }
        return canProtect;
    }

    /**
     * Get disconnectedComponents
     * @return List of (List of Pair(row, column)) of disconnected components
     * @throws IllegalStateException if method called is done in the wrong order
     */
    public List<List<Pair<Integer, Integer>>> getDisconnectedComponents() throws IllegalStateException {
        if (state != 3) {
            throw new IllegalStateException("getDisconnectedComponents done in the wrong order, current state: 3");
        }
        return disconnectedComponents;
    }

    /**
     * Set the protection choice
     * @param protection true if player wants to protect, false otherwise
     * @param ID ID of the energy to use
     * @throws IllegalStateException if method called is done in the wrong order
     */
    public void setProtection(boolean protection, int ID) throws IllegalStateException {
        if (state != 2) {
            throw new IllegalStateException("setProtection done in the wrong order, current state: 2");
        }
        this.protection = protection;
        this.ID = ID;
    }

    /**
     * Set which components to keep: 0 to keep the first, 1 to keep the second, etc.
     * @param toKeepComponents index of the components to keep
     * @throws IllegalStateException if method called is done in the wrong order
     */
    public void setToKeepComponents(int toKeepComponents) throws IllegalStateException {
        if (state != 3) {
            throw new IllegalStateException("setToKeepComponents done in the wrong order, current state: 3");
        }
        this.toKeepComponents = toKeepComponents;
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
        switch (state) {
            case 0:
                throw new IllegalStateException("Dice not set");
            case 1:
                if (hitIndex < 0 || hitIndex >= card.getMeteors().size()) {
                    throw new IndexOutOfBoundsException("hitIndex out of bounds");
                }
                Hit hit = card.getMeteors().get(hitIndex);
                var = player.getSpaceShip().canProtect(dice, hit);
                canProtect = var.getValue1();
                state = 2;
                break;
            case 2:
                if (protection == null) {
                    throw new IllegalStateException("protection is null: Don't know if player wants to protect -> Call setProtection");
                }
                switch (canProtect) {
                    case -1:
                        player.getSpaceShip().destroyComponent(var.getValue0().getRow(), var.getValue0().getColumn());
                        disconnectedComponents = player.getSpaceShip().getDisconnectedComponents();
                        break;
                    case 0:
                        if (!protection) {
                            player.getSpaceShip().destroyComponent(var.getValue0().getRow(), var.getValue0().getColumn());
                            disconnectedComponents = player.getSpaceShip().getDisconnectedComponents();
                        }
                        else {
                            player.getSpaceShip().useEnergy(ID);
                            disconnectedComponents = null;
                        }
                        break;
                    case 1:
                        disconnectedComponents = null;
                        break;
                }
                state = 3;
                toKeepComponents = -1;
                protection = null;
                break;
            case 3:
                if (disconnectedComponents == null || disconnectedComponents.isEmpty()) {
                    state = 0;
                    break;
                }
                if (toKeepComponents < 0 || toKeepComponents >= disconnectedComponents.size()) {
                    throw new IndexOutOfBoundsException("toKeepComponents is out of bounds");
                }
                for (int i = 0; i < disconnectedComponents.size(); i++) {
                    if (i != toKeepComponents) {
                        disconnectedComponents.get(i).forEach(pair -> player.getSpaceShip().destroyComponent(pair.getValue0(), pair.getValue1()));
                    }
                }
                state = 0;
                break;
        }
    }
}
