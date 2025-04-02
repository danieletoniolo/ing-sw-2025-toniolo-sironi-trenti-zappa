package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.Cabin;
import Model.SpaceShip.Component;
import Model.SpaceShip.ComponentType;
import Model.SpaceShip.SpaceShip;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Map;

public class EpidemicState extends State {
    private ArrayList<Component> surroundingComponents;
    private Component component;
    private Map<Integer, Cabin> cabins;
    private Cabin currentCabin;
    private boolean[][] check;

    /**
     * Constructor for EpidemicState
     * @param players List of players in the current order to play
     */
    public EpidemicState(ArrayList<PlayerData> players, Board board) {
        super(players, board);
        check = new boolean[SpaceShip.getRows()][SpaceShip.getCols()];
    }

    /**
     * Function for eliminating the crew in two adjacent cabins
     */
    @Override
    public void entry() {
        for (PlayerData p : players) {
            for(int i = 0; i < SpaceShip.getRows(); i++){
                for(int j = 0; j < SpaceShip.getCols(); j++){
                    check[i][j] = false;
                }
            }
            cabins = p.getSpaceShip().getCabins();
            for(Map.Entry<Integer, Cabin> ca : cabins.entrySet()){
                currentCabin = ca.getValue();
                if(!check[currentCabin.getRow()][currentCabin.getColumn()]){
                    surroundingComponents = p.getSpaceShip().getSurroundingComponents(currentCabin.getRow(), currentCabin.getColumn());
                    for(Component co : surroundingComponents){
                        if(co.getComponentType() == ComponentType.CABIN){
                            check[currentCabin.getRow()][currentCabin.getColumn()] = true;
                            currentCabin.removeCrewMember(1);

                            Cabin cabin = (Cabin) co;
                            if(!check[cabin.getRow()][cabin.getColumn()]){
                                cabin.removeCrewMember(1);
                                check[cabin.getRow()][cabin.getColumn()] = true;
                            }
                        }
                    }
                }
            }
        }
    }
}
