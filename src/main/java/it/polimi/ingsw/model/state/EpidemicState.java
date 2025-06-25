package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.Cabin;
import it.polimi.ingsw.model.spaceship.Component;
import it.polimi.ingsw.model.spaceship.ComponentType;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.spaceship.UpdateCrewMembers;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public class EpidemicState extends State {


    /**
     * Constructor for EpidemicState
     * @param board The board associated with the game
     */
    public EpidemicState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
    }

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state EpidemicState");
    }

    /**
     * Function for eliminating the crew in two adjacent cabins
     */
    @Override
    public void entry() {
        for (PlayerData player : super.players) {
            CurrentPlayer currentPlayer = new CurrentPlayer(player.getUsername());
            eventCallback.trigger(currentPlayer, player.getUUID());
        }
    }

    @Override
    public void execute(PlayerData player) {
        // Create a list to store the IDs, crew numbers and the type of the crew members in each cabin
        ArrayList<Triplet<Integer, Integer, Integer>> cabinsIDs = new ArrayList<>();

        // Initialize a check array to keep track of which cabins have been processed
        boolean[][] check = new boolean[SpaceShip.getRows()][SpaceShip.getCols()];


        // Iterate through the player's spaceship cabins
        List<Cabin> cabins = player.getSpaceShip().getCabins();
        for(Cabin currentCabin : cabins) {

            // If the current cabin has not been processed yet search for surrounding components
            if(!check[currentCabin.getRow()][currentCabin.getColumn()]) {
                check[currentCabin.getRow()][currentCabin.getColumn()] = true;
                ArrayList<Component> surroundingComponents = player.getSpaceShip().getSurroundingComponents(currentCabin.getRow(), currentCabin.getColumn());

                boolean removedFromCurrent = false;
                // Iterate through the surrounding components
                for(Component surroundingComponent : surroundingComponents) {

                    // If the surrounding component is a cabin and has crew members, remove one crew member from the current cabin
                    if(surroundingComponent != null && surroundingComponent.getComponentType() == ComponentType.CABIN && ((Cabin) surroundingComponent).getCrewNumber() > 0) {
                        if (!removedFromCurrent) {
                            currentCabin.removeCrewMember(1);
                            removedFromCurrent = true;
                        }

                        // If we have not already processed the surrounding cabin, remove a crew member from it as well and mark it as processed
                        Cabin surroundingCabin = (Cabin) surroundingComponent;
                        if(!check[surroundingCabin.getRow()][surroundingCabin.getColumn()]) {
                            surroundingCabin.removeCrewMember(1);
                            check[surroundingCabin.getRow()][surroundingCabin.getColumn()] = true;

                            // Add the surrounding cabin's ID, crew numbers and types of the crew members in both cabins to the list of modified cabins
                            cabinsIDs.add(new Triplet<>(surroundingCabin.getID(), surroundingCabin.getCrewNumber(), surroundingCabin.hasBrownAlien() ? 1 : (surroundingCabin.hasPurpleAlien() ? 2 : 0)));
                        }

                        // Add the current cabin's ID, crew number and type of crew member to the list of modified cabins
                        cabinsIDs.add(new Triplet<>(currentCabin.getID(), currentCabin.getCrewNumber(), currentCabin.hasBrownAlien() ? 1 : (currentCabin.hasPurpleAlien() ? 2 : 0)));
                    }
                }
            }
        }

        // Trigger the UpdateCrewMembers event with the modified cabins' IDs and crew information
        UpdateCrewMembers crewEvent = new UpdateCrewMembers(player.getUsername(), cabinsIDs);
        eventCallback.trigger(crewEvent);

        super.execute(player);
        super.nextState(GameState.CARDS);
    }
}
