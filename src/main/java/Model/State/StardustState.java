package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

public class StardustState extends State {

    /**
     * Constructor for StardustState
     * @param players List of players in the current order to play
     */
    public StardustState(ArrayList<PlayerData> players, Board board) {
        super(players, board);
    }

    @Override
    public void entry(){
        for(PlayerData p : players){
            int numberExposedConnectors = p.getSpaceShip().getExposedConnectors();
            board.addSteps(p, -numberExposedConnectors);
        }
    }
}
