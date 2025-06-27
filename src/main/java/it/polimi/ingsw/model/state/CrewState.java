package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.deck.GetShuffledDeck;
import it.polimi.ingsw.event.game.serverToClient.spaceship.SetCannonStrength;
import it.polimi.ingsw.event.game.serverToClient.spaceship.SetEngineStrength;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.spaceship.UpdateCrewMembers;
import it.polimi.ingsw.model.spaceship.Cabin;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
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
    /**
     * Constructs a new CrewState with the specified board, event callback, and state transition handler.
     *
     * @param board the game board
     * @param callback the event callback for triggering events
     * @param transitionHandler the handler for state transitions
     */
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
        if (cabinID >= 152 && cabinID <= 155 && crewType >= 1) {
            throw new IllegalStateException("Cannot place alien in the main cabin");
        }
        UpdateCrewMembers updateCrewMembers;
        ArrayList<Triplet<Integer, Integer, Integer>> crewMembers = new ArrayList<>();

        SpaceShip spaceShip = player.getSpaceShip();
        switch (mode) {
            case 0 -> { // Add crew member
                spaceShip.addCrewMember(cabinID, crewType == 1, crewType == 2);
                crewMembers.add(new Triplet<>(cabinID, player.getSpaceShip().getCabin(cabinID).getCrewNumber(), crewType));
                updateCrewMembers = new UpdateCrewMembers(player.getUsername(), crewMembers);
                eventCallback.trigger(updateCrewMembers);
            }
            case 1 -> { // Remove crew member
                spaceShip.removeCrewMember(cabinID, crewType == 0 ? 2 : 1);
                crewMembers.add(new Triplet<>(cabinID, player.getSpaceShip().getCabin(cabinID).getCrewNumber(), crewType));
                updateCrewMembers = new UpdateCrewMembers(player.getUsername(), crewMembers);
                eventCallback.trigger(updateCrewMembers);
            }
        }

        if (crewType == 1) {
            SetEngineStrength engineStrength = new SetEngineStrength(player.getUsername(), spaceShip.getDefaultEnginesStrength(), spaceShip.getMaxEnginesStrength());
            eventCallback.trigger(engineStrength);
        } else if (crewType == 2) {
            SetCannonStrength cannonStrength = new SetCannonStrength(player.getUsername(), spaceShip.getDefaultCannonsStrength(), spaceShip.getMaxCannonsStrength());
            eventCallback.trigger(cannonStrength);
        }
    }

    /**
     * This method is not supported in CrewState as it is a synchronous state.
     *
     * @return never returns as it always throws an exception
     * @throws SynchronousStateException always thrown since this is a synchronous state
     */
    @Override
    public PlayerData getCurrentPlayer() throws SynchronousStateException {
        throw new SynchronousStateException("Cannot invoke getCurrentPlayer in synchronous state CrewState");
    }

    /**
     * Entry method called when entering the CrewState.
     * In learning mode, automatically fills all cabins with basic crew members (crewType 0).
     * This provides a default crew configuration for new players to understand the game mechanics.
     */
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
        }
    }

    /**
     * Executes the crew state for the specified player.
     * Validates that all cabins have been properly filled with crew members before allowing progression.
     * A cabin is considered properly filled if it has at least 2 crew members OR has at least 1 alien.
     * After validation, transitions to the CARDS game state.
     *
     * @param player the player data for whom to execute the crew state
     * @throws IllegalStateException if any cabin is not properly filled with crew members
     */
    @Override
    public void execute(PlayerData player) {
        // Check if all the cabins have been filled by the player
        for (Cabin cabin : player.getSpaceShip().getCabins()) {
            if ((cabin.getCrewNumber() <= 1 && !cabin.hasPurpleAlien() && !cabin.hasBrownAlien()) ||
                (cabin.getCrewNumber() == 0 && (cabin.hasBrownAlien() || cabin.hasPurpleAlien()))) {
                throw new IllegalStateException("You have to fill all the cabins with crew members before proceeding");
            }
        }

        super.execute(player);
        super.nextState(GameState.CARDS);
    }

    /**
     * Exit method called when leaving the CrewState.
     * Performs cleanup operations inherited from the parent State class.
     */
    @Override
    public void exit() {
        super.exit();
        GetShuffledDeck getShuffledDeckEvent = new GetShuffledDeck(
                board.getShuffledDeck().stream().map(Card::getID).toList()
        );
        eventCallback.trigger(getShuffledDeckEvent);
    }
}
