package Model.State;

import Model.Player.PlayerData;
import org.javatuples.Pair;

import java.util.ArrayList;

public class StardustState extends State {

    /**
     * Constructor for StardustState
     * @param players List of players in the current order to play
     */
    public StardustState(ArrayList<PlayerData> players) {
        super(players);
    }

    @Override
    public void entry(){
        for(Pair<PlayerData, PlayerStatus> p : players){
            int numberExposedConnectors = p.getValue0().getSpaceShip().getExposedConnectors();
            p.getValue0().addSteps(-numberExposedConnectors);
        }
    }
}
