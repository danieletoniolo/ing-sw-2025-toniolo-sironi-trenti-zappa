package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import Model.State.interfaces.ChoosableFragment;
import Model.State.interfaces.DestroyableComponent;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum ValidationInternalState {
    DEFAULT,
    FRAGMENTED_SHIP,
}

public class ValidationState extends State implements ChoosableFragment, DestroyableComponent {
    private ValidationInternalState internalState;
    private Map<PlayerData, ArrayList<Pair<Integer, Integer>>> invalidComponents;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> fragmentedComponents;

    // Tmp variable to store the choice of the fragment
    private int fragmentChoice;
    // Tmp variable to store the choice of the component to destroy
    private ArrayList<Pair<Integer, Integer>> componentsToDestroy;

    public ValidationState(Board board) {
        super(board);
        this.invalidComponents = new HashMap<>();
        this.fragmentedComponents = null;
        this.fragmentChoice = -1;
        this.componentsToDestroy = null;
        this.internalState = ValidationInternalState.DEFAULT;
    }

    public void setFragmentChoice(int fragmentChoice) throws IllegalStateException {
        if (internalState != ValidationInternalState.FRAGMENTED_SHIP) {
            throw new IllegalStateException("No fragment to choose");
        }
        this.fragmentChoice = fragmentChoice;
    }

    public void setComponentToDestroy(PlayerData player, ArrayList<Pair<Integer, Integer>> componentsToDestroy) throws IllegalStateException {
        if (internalState != ValidationInternalState.DEFAULT) {
            throw new IllegalStateException("Cannot destroy componentsToDestroy in this state, you have to choose a fragment");
        }
        this.componentsToDestroy = componentsToDestroy;
    }


    @Override
    public void entry() {
        for (PlayerData p : players) {
            ArrayList<Pair<Integer, Integer>> playerInvalidComponents = p.getSpaceShip().getInvalidComponents();
            if (!playerInvalidComponents.isEmpty()) {
                invalidComponents.put(p, playerInvalidComponents);
                // Set the player status to PLAYING to indicate they have invalid components
                playersStatus.replace(p.getColor(), PlayerStatus.PLAYING);
            }
        }
    }

    @Override
    public void execute(PlayerData player) {
        SpaceShip ship = player.getSpaceShip();
        switch (internalState) {
            case DEFAULT:
                if (componentsToDestroy == null) {
                    throw new IllegalStateException("Player has not set the components to destroy");
                }
                // Get the invalid components of the player
                ArrayList<Pair<Integer, Integer>> playerInvalidComponents = invalidComponents.get(player);
                // Destroy the components given by the player
                for (Pair<Integer, Integer> component : componentsToDestroy) {
                    ship.destroyComponent(component.getValue0(), component.getValue1());
                    playerInvalidComponents.remove(component);
                }
                if (playerInvalidComponents.isEmpty()) {
                    // Check if the ship is now fragmented
                    fragmentedComponents = ship.getDisconnectedComponents();
                    if (fragmentedComponents.size() > 1) {
                        // Set the internal state to FRAGMENTED_SHIP
                        internalState = ValidationInternalState.FRAGMENTED_SHIP;
                    } else {
                        // Reset the fragment components
                        fragmentedComponents = null;
                        // Set the player status to PLAYED
                        playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
                    }
                }
                break;
            case FRAGMENTED_SHIP:
                if (fragmentChoice == -1) {
                    throw new IllegalStateException("Player has not set the fragment choice");
                }
                // Check if the fragment choice is valid
                if (fragmentChoice < 0 || fragmentChoice >= fragmentedComponents.size()) {
                    throw new IndexOutOfBoundsException("Fragment choice is out of bounds");
                }
                // Get the chosen fragment
                ArrayList<Pair<Integer, Integer>> chosenFragment = fragmentedComponents.get(fragmentChoice);
                // Destroy the components in the chosen fragment
                for (Pair<Integer, Integer> component : chosenFragment) {
                    ship.destroyComponent(component.getValue0(), component.getValue1());
                }
                // Reset the fragmented components and the fragment choice
                fragmentedComponents = null;
                fragmentChoice = -1;
                // Switch the internal state to DEFAULT
                internalState = ValidationInternalState.DEFAULT;
                // Set the player status to PLAYED
                playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
        }
    }
    @Override
    public void exit() {
        for (PlayerData p : players) {
            if (!p.getSpaceShip().getInvalidComponents().isEmpty()) {
                throw new IllegalStateException("The player " + p + "has invalid components");
            }
        }
        super.exit();
    }
}

