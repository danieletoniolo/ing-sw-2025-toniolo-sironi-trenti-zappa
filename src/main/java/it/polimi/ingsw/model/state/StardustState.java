package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;

import java.util.ArrayList;


/**
 * StardustState represents the stardust phase of the game where players lose steps
 * based on their exposed connectors. This state reverses the player order and
 * transitions to the CARDS state after execution.
 * @author Vittorio Sironi
 */
public class StardustState extends State {

    /**
     * Constructor for StardustState
     * @param board The board associated with the game
     */
    public StardustState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        super.players = new ArrayList<>(super.players.reversed());
    }

    /**
     * Executes the stardust phase for the given player.
     * During this phase, the player loses steps equal to their number of exposed connectors.
     * After processing the player, triggers events to update the game state and transitions to CARDS state.
     *
     * @param player The player data for whom to execute the stardust phase
     */
    @Override
    public void execute(PlayerData player) {

        int numberExposedConnectors = player.getSpaceShip().getExposedConnectors();
        board.addSteps(player, -numberExposedConnectors);

        MoveMarker stepsEvent = new MoveMarker(player.getUsername(),  player.getModuleStep(board.getStepsForALap()));
        eventCallback.trigger(stepsEvent);

        super.execute(player);

        try {
            CurrentPlayer currentPlayerEvent = new CurrentPlayer(this.getCurrentPlayer().getUsername());
            eventCallback.trigger(currentPlayerEvent);
        }
        catch(Exception e) {
            // Ignore the exception
        }

        super.nextState(GameState.CARDS);
    }

    /**
     * Cleans up resources and performs any necessary finalization when exiting the StardustState.
     * This method is called when transitioning away from the stardust phase.
     */
    @Override
    public void exit() {
        super.exit();
    }
}
