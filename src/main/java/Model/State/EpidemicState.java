package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.Cabin;
import Model.SpaceShip.Component;
import Model.SpaceShip.ComponentType;
import Model.SpaceShip.SpaceShip;
import controller.event.game.CrewLoss;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Map;

public class EpidemicState extends State {
    private ArrayList<Component> surroundingComponents;
    private Map<Integer, Cabin> cabins;
    private Cabin currentCabin;
    private boolean[][] check;

    /**
     * Constructor for EpidemicState
     * @param board The board associated with the game
     */
    public EpidemicState(Board board) {
        super(board);
        check = new boolean[SpaceShip.getRows()][SpaceShip.getCols()];
    }

    /**
     * Function for eliminating the crew in two adjacent cabins
     */
    @Override
    public void entry() {
        ArrayList<Pair<Integer, Integer>> cabinsIDs;
        for (PlayerData p : players) {
            cabinsIDs = new ArrayList<>();

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
                        if(co != null && co.getComponentType() == ComponentType.CABIN){
                            if (!check[currentCabin.getRow()][currentCabin.getColumn()]) {
                                check[currentCabin.getRow()][currentCabin.getColumn()] = true;
                                currentCabin.removeCrewMember(1);
                            }

                            Cabin cabin = (Cabin) co;
                            if(!check[cabin.getRow()][cabin.getColumn()]){
                                cabin.removeCrewMember(1);
                                check[cabin.getRow()][cabin.getColumn()] = true;
                                cabinsIDs.add(new Pair<>(cabin.getID(), 1));
                            }
                        }
                    }
                }
            }

            // TODO: EVENT CREWLOSS
            CrewLoss crewEvent = new CrewLoss(p.getColor(), cabinsIDs);
        }
    }
}
