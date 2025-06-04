package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.player.MoveMarker;


public class StardustState extends State {

    /**
     * Constructor for StardustState
     * @param board The board associated with the game
     */
    public StardustState(Board board, EventCallback callback, StateTransitionHandler transitionHandler) {
        super(board, callback, transitionHandler);
    }

    @Override
    public void entry(){
        for(PlayerData p : players){
            int numberExposedConnectors = p.getSpaceShip().getExposedConnectors();
            board.addSteps(p, -numberExposedConnectors);

            MoveMarker stepsEvent = new MoveMarker(p.getUsername(), p.getStep());
            eventCallback.trigger(stepsEvent);
        }
        super.entry();
    }

    @Override
    public void execute(PlayerData player) {
        super.execute(player);
        super.nextState(GameState.CARDS);
    }
}
