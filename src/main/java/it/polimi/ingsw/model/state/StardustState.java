package it.polimi.ingsw.model.state;

import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.event.game.serverToClient.MoveMarker;


public class StardustState extends State {

    /**
     * Constructor for StardustState
     * @param board The board associated with the game
     */
    public StardustState(Board board, EventCallback callback) {
        super(board, callback);
    }

    @Override
    public void entry(){
        for(PlayerData p : players){
            int numberExposedConnectors = p.getSpaceShip().getExposedConnectors();
            board.addSteps(p, -numberExposedConnectors);

            MoveMarker stepsEvent = new MoveMarker(p.getUsername(), p.getStep());
            eventCallback.trigger(stepsEvent);
        }
    }
}
