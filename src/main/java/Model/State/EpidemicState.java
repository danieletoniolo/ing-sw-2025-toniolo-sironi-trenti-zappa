package Model.State;

import Model.Game.Board.Board;
import Model.Player.PlayerData;
import Model.SpaceShip.Cabin;
import Model.SpaceShip.Component;
import Model.SpaceShip.ComponentType;
import Model.SpaceShip.SpaceShip;
import controller.EventCallback;
import event.game.serverToClient.UpdateCrewMembers;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public class EpidemicState extends State {
    private final boolean[][] check;

    /**
     * Constructor for EpidemicState
     * @param board The board associated with the game
     */
    public EpidemicState(Board board, EventCallback callback) {
        super(board, callback);
        check = new boolean[SpaceShip.getRows()][SpaceShip.getCols()];
    }

    /**
     * Function for eliminating the crew in two adjacent cabins
     */
    @Override
    public void entry() {
        ArrayList<Triplet<Integer, Integer, Integer>> cabinsIDs;
        for (PlayerData p : players) {
            cabinsIDs = new ArrayList<>();

            for(int i = 0; i < SpaceShip.getRows(); i++){
                for(int j = 0; j < SpaceShip.getCols(); j++){
                    check[i][j] = false;
                }
            }

            List<Cabin> cabins = p.getSpaceShip().getCabins();
            for(Cabin ca : cabins){
                if(!check[ca.getRow()][ca.getColumn()]){
                    ArrayList<Component> surroundingComponents = p.getSpaceShip().getSurroundingComponents(ca.getRow(), ca.getColumn());
                    for(Component co : surroundingComponents){
                        if(co != null && co.getComponentType() == ComponentType.CABIN && ((Cabin) co).getCrewNumber() > 0){
                            if (!check[ca.getRow()][ca.getColumn()]) {
                                check[ca.getRow()][ca.getColumn()] = true;
                                ca.removeCrewMember(1);
                            }

                            Cabin cabin = (Cabin) co;
                            if(!check[cabin.getRow()][cabin.getColumn()]){
                                cabin.removeCrewMember(1);
                                check[cabin.getRow()][cabin.getColumn()] = true;
                            }
                        }
                    }
                }
                cabinsIDs.add(new Triplet<>(ca.getID(), ca.getCrewNumber(), 0));
            }

            UpdateCrewMembers crewEvent = new UpdateCrewMembers(p.getUsername(), cabinsIDs);
            eventCallback.trigger(crewEvent);
        }
    }
}
