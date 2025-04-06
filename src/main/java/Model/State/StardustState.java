package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

public class StardustState extends State {

    /**
     * Constructor for StardustState
     * @param board The board associated with the game
     */
    public StardustState(Board board) {
        super(board);
    }

    @Override
    public void entry(){
        for(PlayerData p : players){
            int numberExposedConnectors = p.getSpaceShip().getExposedConnectors();
            board.addSteps(p, -numberExposedConnectors);
        }
    }
}
