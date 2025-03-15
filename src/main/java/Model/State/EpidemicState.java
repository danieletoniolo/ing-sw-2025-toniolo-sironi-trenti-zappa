package Model.State;

import Model.Player.PlayerData;
import Model.SpaceShip.Cabin;
import Model.SpaceShip.Component;
import Model.SpaceShip.ComponentType;
import org.javatuples.Pair;

import java.util.ArrayList;

public class EpidemicState extends State {
    private ArrayList<Component> surroundingComponents;
    private Component component;
    private Boolean[][] check;

    /**
     * Constructor for EpidemicState
     * @param players List of players in the current order to play
     */
    public EpidemicState(ArrayList<PlayerData> players) {
        super(players);
    }

    @Override
    public void entry() {
        check = new Boolean[12][12];
        for (Pair<PlayerData , PlayerStatus> p : players) {
            for(int i = 0; i < 12; i++){
                for(int j = 0; j < 12; j++){
                    if(p.getValue0().getSpaceShip().getComponent(i, j) != null){
                        check[i][j] = false;
                    } else {
                        check[i][j] = null;
                    }
                }
            }
            for(int i = 0; i < 12; i++){
                for(int j = 0; j < 12; j++){
                    if(p.getValue0().getSpaceShip().getComponent(i, j) != null && !check[i][j]) {
                        check[i][j] = true;
                        component = p.getValue0().getSpaceShip().getComponent(i, j);
                        if(component.getComponentType() == ComponentType.CABIN){
                            surroundingComponents = p.getValue0().getSpaceShip().getSurroundingComponents(i, j);
                            for(Component c : surroundingComponents){
                                if(c.getComponentType() == ComponentType.CABIN){
                                    Cabin cabin1 = (Cabin) component;

                                    if(cabin1.hasPurpleAlien() || cabin1.hasBrownAlien()){
                                        cabin1.removeAlien();
                                    } else {
                                        cabin1.removeCrewMember();
                                    }

                                    int row, column;
                                    row = c.getRow();
                                    column = c.getColumn();

                                    if(!check[row][column]){
                                        Cabin cabin2 = (Cabin) c;

                                        if(cabin2.hasPurpleAlien() || cabin2.hasBrownAlien()){
                                            cabin2.removeAlien();
                                        } else {
                                            cabin2.removeCrewMember();
                                        }

                                        check[row][column] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
