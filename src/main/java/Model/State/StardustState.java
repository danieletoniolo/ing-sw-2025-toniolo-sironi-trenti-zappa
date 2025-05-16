package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import controller.EventCallback;
import event.game.MoveMarker;


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
