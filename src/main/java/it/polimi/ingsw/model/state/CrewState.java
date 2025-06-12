package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.spaceship.UpdateCrewMembers;
import it.polimi.ingsw.model.spaceship.Cabin;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the state of the game when the player is managing their crew members.
 * It extends the State class and implements the manageCrewMember method.
 * @see State
 * @author Daniele Toniolo
 */
public class CrewState extends State {
    public CrewState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
    }

    /**
     * Implementation of the method to manage crew members
     * @implNote In this state the removal of crew members is allowed just to erase the whole cabin if
     * the player wants to change its crew distribution.
     * @see State#manageCrewMember(PlayerData, int, int, int)
     */
    public void manageCrewMember(PlayerData player, int mode, int crewType, int cabinID) {
        if (super.played) {
            throw new IllegalStateException("This state has already been played");
        }
        UpdateCrewMembers updateCrewMembers;
        ArrayList<Triplet<Integer, Integer, Integer>> crewMembers = new ArrayList<>();

        switch (mode) {
            case 0 -> { // Add crew member
                player.getSpaceShip().addCrewMember(cabinID, crewType == 1, crewType == 2);
                crewMembers.add(new Triplet<>(cabinID, player.getSpaceShip().getCabin(cabinID).getCrewNumber(), crewType));
                updateCrewMembers = new UpdateCrewMembers(player.getUsername(), crewMembers);
                eventCallback.trigger(updateCrewMembers);
            }
            case 1 -> { // Remove crew member
                player.getSpaceShip().removeCrewMember(cabinID, crewType == 0 ? 2 : 1);
                crewMembers.add(new Triplet<>(cabinID, player.getSpaceShip().getCabin(cabinID).getCrewNumber(), crewType));
                updateCrewMembers = new UpdateCrewMembers(player.getUsername(), crewMembers);
                eventCallback.trigger(updateCrewMembers);
            }
        }
    }

    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in a synchronous state");
    }

    @Override
    public void entry() {
        if (board.getBoardLevel() == Level.LEARNING) {
            for (PlayerData player : players) {
                SpaceShip ship = player.getSpaceShip();
                List<Cabin> cabins = ship.getCabins();
                for (Cabin cabin : cabins) {
                    manageCrewMember(player, 0, 0, cabin.getID());
                }
            }
            super.played = true;
        }
        super.entry();
    }

    @Override
    public void execute(PlayerData player) {
        // Check if all the cabins have been filled by the player
        for (PlayerData p : players) {
            for (Cabin cabin : p.getSpaceShip().getCabins()) {
                if ((cabin.getCrewNumber() <= 1 && !cabin.hasPurpleAlien() && !cabin.hasBrownAlien()) ||
                     cabin.getCrewNumber() == 0 && (cabin.hasBrownAlien()) || cabin.hasPurpleAlien()) {
                    throw new IllegalStateException("You have to fill all the cabins with crew members before proceeding");
                }
            }
        }
        super.execute(player);
        super.nextState(GameState.CARDS);
    }

    @Override
    public void exit() {
        super.exit();
    }
}
