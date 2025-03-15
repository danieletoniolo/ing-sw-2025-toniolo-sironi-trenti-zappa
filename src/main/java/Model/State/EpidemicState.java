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
    private boolean[][] check;

    /**
     * Constructor for EpidemicState
     * @param players List of players in the current order to play
     */
    public EpidemicState(ArrayList<PlayerData> players) {
        super(players);
    }

    @Override
    public void entry() {
        check = new boolean[12][12];
        for (Pair<PlayerData , Boolean> p : players) {
            for(int i = 0; i < 12; i++){
                for(int j = 0; j < 12; j++){
                    if(p.getValue0().getSpaceShip().getComponent(i, j) != null){
                        check[i][j] = false;
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

    /**
     * Marks the player as played
     * @param player PlayerData of the player to play
     */
    @Override
    public void execute(PlayerData player) {
        for (Pair<PlayerData , Boolean> p : players) {
            if (p.getValue0().equals(player)) {
                p.setAt1(true);
                break;
            }
        }
    }

    /**
     * Exits the state and removes the flight days from the players that have selected a planet
     * If a player has not selected a planet, the flight days are not removed
     */
    @Override
    public void exit() {
        super.exit();
    }

}
