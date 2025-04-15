package Model;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Random;

public class GenerateRandomShip {
    SpaceShip ship;

    public GenerateRandomShip() throws JsonProcessingException {
        Random rand = new Random();

        //Creation valid spots
        boolean[][] vs = new boolean[12][12];
        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 12; j++) {
                vs[i][j] = false;
            }
        }
        vs[5][6] = true;
        vs[5][8] = true;
        for(int j = 5; j < 10; j++) {
            vs[6][j] = true;
        }
        for(int i = 4; i < 11; i++) {
            vs[7][i] = true;
            vs[8][i] = true;
        }
        for(int j = 4; j < 7; j++) {
            vs[9][j] = true;
        }
        for(int j = 8; j < 11; j++) {
            vs[9][j] = true;
        }

        SpaceShip s = new SpaceShip(Level.SECOND, vs);
        Board b = new Board(Level.SECOND);
        Component c = null;

        //Insert components
        //Start from the center
        int minRiga = 7;
        int maxRiga = 7;
        int minColonna = 7;
        int maxColonna = 7;
        int count = 1;
        vs[7][7] = false;
        ArrayList<Integer> used_id = new ArrayList<>();

        while (count < 12 * 12) {

            if (maxColonna < 12 - 1) maxColonna++;
            for (int i = minColonna; i <= maxColonna; i++) {
                if (vs[minRiga][i]) {
                    int id;
                    do {
                        id = rand.nextInt(0, 156);
                    }while(used_id.contains(id));
                    used_id.add(id);
                    //c = b.getTile(id);
                    s.placeComponent(c, minRiga, i);
                    vs[minRiga][i] = false;
                    count++;
                }
            }

            if (maxRiga < 12 - 1) maxRiga++;
            for (int i = minRiga + 1; i <= maxRiga; i++) {
                if (vs[i][maxColonna]) {
                    int id;
                    do {
                        id = rand.nextInt(0, 156);
                    }while(used_id.contains(id));
                    used_id.add(id);
                    //c = b.getTile(id);
                    s.placeComponent(c, i, maxColonna);
                    vs[i][maxColonna] = false;
                    count++;
                }
            }

            if (minColonna > 0) minColonna--;
            for (int i = maxColonna - 1; i >= minColonna; i--) {
                if (vs[maxRiga][i]) {
                    int id;
                    do {
                        id = rand.nextInt(0, 156);
                    }while(used_id.contains(id));
                    used_id.add(id);
                    //c = b.getTile(id);
                    s.placeComponent(c, maxRiga, i);
                    vs[maxRiga][i] = false;
                    count++;
                }
            }

            if (minRiga > 0) minRiga--;
            for (int i = maxRiga - 1; i > minRiga; i--) {
                if (vs[i][minColonna]) {
                    int id;
                    do {
                        id = rand.nextInt(0, 156);
                    }while(used_id.contains(id));
                    used_id.add(id);
                    //c = b.getTile(id);
                    s.placeComponent(c, i, minColonna);
                    vs[i][minColonna] = false;
                    count++;
                }
            }

            if(count == 27){
                break;
            }
        }

        this.ship = s;
    }

    public void addElementsShip(){
        SpaceShip s = this.ship;
        boolean pDone = false;
        boolean bDone = false;

        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 12; j++){
                if(s.getComponent(i, j) != null){
                    Component c = s.getComponent(i, j);
                    switch(c.getComponentType()){
                        case CABIN, CENTER_CABIN:
                            ArrayList<Component> cs = s.getSurroundingComponents(i, j);
                            boolean purple = false;
                            boolean brown = false;

                            for(Component c1 : cs){
                                if(c1 != null) {
                                    if (c1.getComponentType() == ComponentType.PURPLE_LIFE_SUPPORT && !pDone) {
                                        purple = true;
                                        pDone = true;
                                        break;
                                    } else if (c1.getComponentType() == ComponentType.BROWN_LIFE_SUPPORT && !bDone) {
                                        brown = true;
                                        bDone = true;
                                        break;
                                    }
                                }
                            }
                            if(purple){
                                Cabin cabin = (Cabin) c;
                                if(!cabin.hasBrownAlien() && !cabin.hasPurpleAlien()) {
                                    cabin.isValid();
                                    s.addCrewMember(cabin.getID(), false, true);
                                    cabin.addPurpleAlien();
                                }
                            } else if(brown){
                                Cabin cabin = (Cabin) c;
                                if(!cabin.hasPurpleAlien() && !cabin.hasBrownAlien()) {
                                    cabin.isValid();
                                    s.addCrewMember(cabin.getID(), true, false);
                                    cabin.addBrownAlien();
                                }
                            } else {
                                Cabin cabin = (Cabin) c;
                                cabin.addCrewMember();
                                s.addCrewMember(cabin.getID(), false, false);
                            }
                            break;
                        case STORAGE:
                            Storage storage = (Storage) c;
                            Random rand = new Random();
                            ArrayList<Good> goods = new ArrayList<>();
                            for(int l = 0; l < storage.getGoodsCapacity(); l++){
                                GoodType[] values = GoodType.values();
                                int randomIndex = rand.nextInt(values.length);
                                GoodType randomGoodType = values[randomIndex];
                                Good g = new Good(randomGoodType);
                                if(randomGoodType == GoodType.RED && !storage.isDangerous()){
                                    l--;
                                } else {
                                    goods.add(g);
                                }
                            }
                            ship.exchangeGood(goods, null, storage.getID());
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public SpaceShip getShip() {
        return this.ship;
    }
}
