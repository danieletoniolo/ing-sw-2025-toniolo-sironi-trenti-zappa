package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.game.serverToClient.player.CurrentPlayer;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;

import java.util.ArrayList;


public class StardustState extends State {

    /**
     * Constructor for StardustState
     * @param board The board associated with the game
     */
    public StardustState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
        super.players = new ArrayList<>(super.players.reversed());
    }

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

    @Override
    public void exit() {
        super.exit();
    }
}
