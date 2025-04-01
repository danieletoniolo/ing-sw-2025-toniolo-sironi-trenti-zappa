package Model.SpaceShip;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Game.Board.Level;
import Model.GenerateRandomShip;
import Model.Good.Good;
import Model.Good.GoodType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SpaceShipTest {
    SpaceShip ship;
    boolean[][] spots;
    ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};

    @BeforeEach
    void setUp() {
        spots = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                spots[i][j] = true;
            }
        }
        ship = new SpaceShip(Level.SECOND, spots);
        assertNotNull(ship, "Ship not initialized correctly");
    }

    @RepeatedTest(5)
    void getNumberOfComponents() {
        assertEquals(1, ship.getNumberOfComponents());

        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            ship.placeComponent(storage, 6, 7 + i);
        }
        assertEquals(count + 1, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getSingleEnginesStrength() {
        assertEquals(0, ship.getSingleEnginesStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " engine");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Engine engine = new Engine(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                engine.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            if(engine.getClockwiseRotation() != 0){
                System.out.println("Engine " + j + " cannot be added");
            } else {
                ship.placeComponent(engine, 8,7 + index);
                index++;
                if(engine.getComponentType() == ComponentType.SINGLE_ENGINE){
                    power += i;
                }
            }

            System.out.println(" ");
        }

        assertEquals(power, ship.getSingleEnginesStrength());
    }

    @RepeatedTest(5)
    void getDoubleEnginesStrength() {
        assertEquals(0, ship.getDoubleEnginesStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " engine");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Engine engine = new Engine(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                engine.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            if(engine.getClockwiseRotation() != 0){
                System.out.println("Engine " + j + " cannot be added");
            } else {
                ship.placeComponent(engine, 8,7 + index);
                index++;
                if(engine.getComponentType() == ComponentType.DOUBLE_ENGINE){
                    power += 2;
                }
            }

            System.out.println(" ");
        }

        assertEquals(power, ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(500)
    void checkTotalEngineStrength() {
        assertEquals(0, ship.getSingleEnginesStrength() + ship.getDoubleEnginesStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        for(j = 0; j < rand.nextInt(5); j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Engine engine = new Engine(0, connectors, i);
            power = power + i;
            ship.placeComponent(engine, 8,7 + j);
        }
        System.out.println(" power " + power);
        assertEquals(power, ship.getSingleEnginesStrength() + ship.getDoubleEnginesStrength());
    }

    @RepeatedTest(5)
    void getSingleCannonsStrength() {
        assertEquals(0, ship.getSingleCannonsStrength());

        Random rand = new Random();
        int j;
        int i;
        float power = 0f;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " cannon");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Cannon cannon = new Cannon(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6,7 + index);
            if(cannon.getComponentType() == ComponentType.SINGLE_CANNON){
                System.out.println("Cannon " + j + " is good");
                if(cannon.getClockwiseRotation() != 0){
                    power += 0.5f;
                } else {
                    power += 1f;
                }
            }
            index++;

            System.out.println(" ");
        }
        System.out.println(" power : " + power);
        assertEquals(power, ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void getDoubleCannonsStrength() {
        assertEquals(0, ship.getDoubleCannonsStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        int index = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " cannon");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Cannon cannon = new Cannon(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6,7 + index);
            if(cannon.getComponentType() == ComponentType.DOUBLE_CANNON){
                System.out.println("Cannon " + j + " is good");
                if(cannon.getClockwiseRotation() != 0){
                    power += 1;
                } else {
                    power += 2;
                }
            }
            index++;

            System.out.println(" ");
        }
        System.out.println(" power : " + power);
        assertEquals(power, ship.getDoubleCannonsStrength());
    }

    @RepeatedTest(5)
    void getDoubleCannonsNumber() {
        assertEquals(0, ship.getDoubleCannonsNumber());

        Random rand = new Random();
        int j;
        int i;
        int power = 0;
        int index = 0;
        int number = 0;
        int limit = rand.nextInt(3) + 1;
        System.out.println("There are " + limit + " cannon");
        for(j = 0; j < limit; j++){
            i = rand.nextInt(2) + 1;
            System.out.println(" i " + i);
            Cannon cannon = new Cannon(j, connectors, i);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6,7 + index);
            if(cannon.getComponentType() == ComponentType.DOUBLE_CANNON){
                System.out.println("Cannon " + j + " is good");
                if(cannon.getClockwiseRotation() != 0){
                    power += 1;
                } else {
                    power += 2;
                }
                number++;
            }
            index++;

            System.out.println(" ");
        }
        System.out.println(" number : " + number);
        assertEquals(number, ship.getDoubleCannonsNumber());
    }

    @RepeatedTest(5)
    void checkTotalCannonsStrength() {
        assertEquals(0, ship.getDoubleCannonsStrength() + ship.getSingleCannonsStrength());

        Random rand = new Random();
        int j;
        int i, power = 0;
        for(j = 0; j < rand.nextInt(5); j++){
            i = rand.nextInt(2) + 1;
            Cannon cannon = new Cannon(j, connectors, i);
            power += i;
            ship.placeComponent(cannon, 6,7 + j);
        }

        assertEquals(power, ship.getDoubleCannonsStrength() + ship.getSingleCannonsStrength());
    }

    @RepeatedTest(5)
    void getEnergyNumber() {
        assertEquals(0, ship.getEnergyNumber());

        Random rand = new Random();
        int limit = rand.nextInt(3);
        int index = 0;
        int total = 0;
        for(int i = 0; i < limit; i++){
            int j = rand.nextInt(2,4);
            Battery battery = new Battery(i, connectors, j);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                battery.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(battery, 6,7 + index);
            index++;
            total += j;
        }

        assertEquals(total, ship.getEnergyNumber());
    }

    @RepeatedTest(5)
    void exchangeGood() {
        assertEquals(0, ship.getGoodsValue());

        Random rand = new Random();
        int limit = rand.nextInt(4);
        int index = 0;
        int total = 0;
        for(int i = 0; i < limit; i++){
            System.out.println("i : " + i + "\n");
            boolean bool = rand.nextBoolean();
            Storage storage = new Storage(i, connectors, bool, 3);

            ship.placeComponent(storage, 6,7 + index);
            index++;

            //Creation of storage
            ArrayList<Good> gToAdd = new ArrayList<>();
            for(int k = 0; k < 3; k++){
                GoodType[] values = GoodType.values();
                GoodType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                Good good = new Good(randomType);

                if(storage.isDangerous()){
                    gToAdd.add(good);
                    total += good.getValue();
                } else {
                    if(good.getColor() != GoodType.RED){
                        gToAdd.add(good);
                        total += good.getValue();
                    } else {
                        System.out.println("Red good not added");
                        // k--; If we want to add another good
                    }
                }
            }
            ship.exchangeGood(gToAdd, null, storage.getID());

            System.out.println(" ");

            //Add
            ArrayList<Good> addGoods = new ArrayList<>();
            int toAdd = rand.nextInt(1, storage.getGoodsCapacity());
            for(int k = 0; k < toAdd; k++){
                GoodType[] values = GoodType.values();
                GoodType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                Good good = new Good(randomType);

                if(storage.isDangerous()){
                    addGoods.add(good);
                    total += good.getValue();
                } else {
                    if(good.getColor() != GoodType.RED){
                        addGoods.add(good);
                        total += good.getValue();
                    } else {
                        System.out.println("Red good not added");
                        k--;
                    }
                }
            }

            System.out.println(" ");

            //Remove
            ArrayList<Good> removeGoods = new ArrayList<>();
            for(int k = 0; k < toAdd; k++){
                Good good = storage.getGoods().get(k);
                System.out.println(good.getColor());
                removeGoods.add(good);
                total -= good.getValue();
            }

            ship.exchangeGood(addGoods, removeGoods, storage.getID());
        }
        System.out.println(total);
        assertEquals(total, ship.getGoodsValue());
    }

    //Test for CrewMember and Aliens
    @RepeatedTest(50)
    void crewNumber(){
        assertEquals(0, ship.getCrewNumber());

        Random rand = new Random();
        int limit = rand.nextInt(1, 4);
        System.out.println("How many: " + limit);
        int total = 0;
        for(int i = 0; i < limit; i++){
            Cabin cabin = new Cabin(i + 4, connectors);
            System.out.println(cabin);
            assertThrows((IllegalArgumentException.class), () -> ship.addCrewMember(cabin.getID(), false, false), "N1");

            ship.placeComponent(cabin, 6,7 + i);

            assertThrows((IllegalArgumentException.class), () -> ship.addCrewMember(cabin.getID(), true, true));

            int flag = rand.nextInt(1, 3);
            System.out.println("Flag: " + flag);
            if(flag == 1 && (!ship.hasPurpleAlien() || !ship.hasBrownAlien())) {
                if (!ship.hasBrownAlien()) {
                    LifeSupportBrown lsb = new LifeSupportBrown(i + 10, connectors);
                    ship.placeComponent(lsb, 5,7 + i);

                    ship.getCabin(i + 4).isValid();
                    ship.addCrewMember(cabin.getID(), true, false);
                    total += 1;
                    System.out.println("BA");

                    assertTrue(ship.hasBrownAlien());
                    assertEquals(1, cabin.getCrewNumber());
                } else {
                    LifeSupportPurple lsp = new LifeSupportPurple(i + 11, connectors);
                    ship.placeComponent(lsp, 5,7 + i);

                    ship.getCabin(i + 4).isValid();
                    ship.addCrewMember(cabin.getID(), false, true);
                    total += 1;
                    System.out.println("PA");

                    assertTrue(ship.hasPurpleAlien());
                    assertEquals(1, cabin.getCrewNumber());
                }
            } else {
                ship.addCrewMember(cabin.getID(), false, false);
                total += 2;
                System.out.println("CM");
                assertEquals(2, cabin.getCrewNumber());
            }
        }

        System.out.println("Total : " + total +"\n");
        assertEquals(total, ship.getCrewNumber());

        for(int j = 0; j < limit; j++){
            boolean flag = rand.nextBoolean();
            System.out.println("Flag : " + flag + "    ID: " + (j));
            if(flag){           //I need to remove
                if (ship.getCabin(j + 4).hasBrownAlien()) {
                    System.out.println("BA.1");

                    ship.removeCrewMember(j + 4, 1);
                    assertFalse(ship.hasBrownAlien());
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 1;
                } else if(ship.getCabin(j + 4).hasPurpleAlien()) {
                    System.out.println("PA.1");

                    ship.removeCrewMember(j + 4, 1);
                    assertFalse(ship.hasPurpleAlien());
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 1;
                } else {
                    System.out.println("CM.1");

                    ship.removeCrewMember(j + 4, 2);
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 2;
                }
            }
        }

        System.out.println("Total final : " + total + "\n");
        assertEquals(total, ship.getCrewNumber());
    }

    @RepeatedTest(500)
    void canProtect(){
        Random rand = new Random();
        int numberHit = rand.nextInt(3, 6);
        Pair<Component, Integer> pair;

        Shield shield = new Shield(10, connectors);
        for(int j = 0; j < rand.nextInt(4); j++){
            shield.rotateClockwise();
        }
        System.out.println("Rotation shield: " + shield.getClockwiseRotation());
        ship.placeComponent(shield, 6,7);

        Battery battery = new Battery(11, connectors, 3);
        ship.placeComponent(battery, 6,8);
        Battery battery1 = new Battery(12, connectors, 3);
        ship.placeComponent(battery1, 8,7);
        ConnectorType[] connector1 = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Battery battery2 = new Battery(13, connector1, 3);
        ship.placeComponent(battery2, 8,6);
        Battery battery3 = new Battery(12, connectors, 3);
        ship.placeComponent(battery3, 9,7);

        Cannon cannon = new Cannon(12, connectors, 1);
        for(int j = 0; j < rand.nextInt(4); j++){
            cannon.rotateClockwise();
        }
        System.out.println("Rotation cannon: " + cannon.getClockwiseRotation());
        ship.placeComponent(cannon, 6,9);

        for(int j = 0; j < numberHit; j++){
            int dice = rand.nextInt(6,10);
            System.out.println("\nDice : " + dice);

            HitType[] values = HitType.values();
            HitType randomType = values[rand.nextInt(values.length)];
            System.out.println(randomType);

            Direction[] values2 = Direction.values();
            Direction randomDirection = values2[rand.nextInt(values2.length)];
            System.out.println(randomDirection);

            Hit hit = new Hit(randomType, randomDirection);

            pair = ship.canProtect(dice, hit);
            System.out.println(pair.getValue0() + "  " + pair.getValue1());

            if(hit.getType() == HitType.SMALLMETEOR){
                Component component = null;
                Component[][] components = ship.getComponents();
                switch (hit.getDirection()) {
                    case NORTH:
                        for (int i = 0; i < 12 && component == null; i++) {
                            component = components[i][dice];
                        }
                        break;
                    case WEST:
                        for (int i = 0; i < 12 && component == null; i++) {
                            component = components[dice][i];
                        }
                        break;
                    case SOUTH:
                        for (int i = 12 - 1; i >= 0 && component == null; i--) {
                            component = components[i][dice];
                        }
                        break;
                    case EAST:
                        for (int i = 12 - 1; i >= 0 && component == null; i--) {
                            component = components[dice][i];
                        }
                        break;
                }
                if (component != null) {
                    if (component.getConnection(hit.getDirection().getValue()) == ConnectorType.EMPTY) {
                        assertEquals(1, pair.getValue1());
                    }
                } else if(hit.getDirection().getValue() == shield.getClockwiseRotation() || hit.getDirection().getValue() == (shield.getClockwiseRotation() + 3) % 4){
                    assertEquals(0, pair.getValue1());
                } else {
                    assertEquals(-1, pair.getValue1());
                }
            } else if(hit.getType() == HitType.LARGEMETEOR){
                if(hit.getDirection().getValue() == cannon.getClockwiseRotation() && pair.getValue0() != null){
                    if(hit.getDirection().getValue() % 2 == 0 && dice == cannon.getColumn()){
                        assertEquals(1, pair.getValue1());
                    } else if(hit.getDirection().getValue() % 2 != 0 && dice == cannon.getRow()){
                        assertEquals(1, pair.getValue1());
                    } else {
                        assertEquals(-1, pair.getValue1());
                    }
                } else {
                    if(pair.getValue0() == null){
                        assertEquals(1, pair.getValue1());
                    } else {
                        assertEquals(-1, pair.getValue1());
                    }
                }
            } else if(hit.getType() == HitType.LIGHTFIRE){
                if(hit.getDirection().getValue() == shield.getClockwiseRotation() || hit.getDirection().getValue() == (shield.getClockwiseRotation() + 3) % 4){
                    assertEquals(0, pair.getValue1());
                } else {
                    assertEquals(-1, pair.getValue1());
                }
            } else {
                assertEquals(-1, pair.getValue1());
            }

        }
    }

    //TODO: Control if in the model they put false or they throws an exception
    @RepeatedTest(5)
    void useEnergy() {
        Random rand = new Random();
        Map<Integer, Battery> batteries;
        int count = rand.nextInt(3) + 1;
        for(int i = 0; i < count; i++){
            int energy = rand.nextInt(2, 4);
            System.out.println("Battery " + i + " : " + energy);
            Battery battery = new Battery(i + 4, connectors, energy);
            for(int j = 0; j < rand.nextInt(4); j++){
                battery.rotateClockwise();
            }
            ship.placeComponent(battery, 6,7 + i);
        }

        batteries = ship.getBatteries();
        assertEquals(count, batteries.size());

        for(int i = 0; i < count; i++){
            Battery batterySingle = batteries.get(i + 4);
            int energy = batterySingle.getEnergyNumber();
            System.out.println(energy);
            if(energy > 0){
                assertTrue(ship.useEnergy(batterySingle.ID));
                System.out.println(batterySingle.getEnergyNumber());
                assertEquals(energy - 1, batterySingle.getEnergyNumber());
            } else {
                assertFalse(ship.useEnergy(batterySingle.ID));
            }
        }

    }

    @RepeatedTest(5)
    void getInvalidComponents() {
        Random rand = new Random();
        ConnectorType[] values = ConnectorType.values();
        List<Pair<Integer, Integer>> invalidComponents;
        boolean shipOk = true;

        int count = rand.nextInt(1, 4);
        for(int j = 0; j < count; j++){
            ConnectorType[] connector = new ConnectorType[4];
            for(int i = 0; i < 4; i++){
                ConnectorType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                connector[i] = randomType;
            }

            Storage storage = new Storage(j + 4, connector, true, 2);
            System.out.println(storage + "\n");
            ship.placeComponent(storage, 6, 7 + j);

            if(!storage.isValid()){
                shipOk = false;
            }
        }
        System.out.println(shipOk);

        invalidComponents = ship.getInvalidComponents();
        for (Pair<Integer, Integer> invalidComponent : invalidComponents) {
            System.out.println(ship.getComponent(invalidComponent.getValue0(), invalidComponent.getValue1()));
        }
        System.out.println();

        if(!invalidComponents.isEmpty()){
            System.out.println(" No " + invalidComponents);
            assertFalse(shipOk);
        } else {
            System.out.println(" Si ");
            assertTrue(shipOk);
        }
        System.out.println();

    }

    @RepeatedTest(5)
    void refreshExposedConnectors() {
        ship.refreshExposedConnectors();
        Component c = ship.getComponent(7, 7);
        System.out.println(c.getExposedConnectors());
        assertEquals(4, ship.getExposedConnectors());

        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        int ris = 4;
        Storage[] storage = new Storage[count];
        for(int i = 0; i < count; i++){
            ris = 3; //Because when I add the first component, the exposed connectors are 3 for the central component
            storage[i] = new Storage(i, connectors, true, 2);
            ship.placeComponent(storage[i], 6, 7 + i);
            ship.refreshExposedConnectors();
            for(int j = 0; j <= i; j++){
                ris += storage[j].getExposedConnectors();
            }
            System.out.println(ris);
        }
        assertEquals(ris, ship.getExposedConnectors());
    }

    //All the components are in direction 0
    @RepeatedTest(5)
    void getComponent() throws JsonProcessingException {
        Random rand = new Random();
        ComponentType[] values = ComponentType.values();
        ComponentType randomType = values[rand.nextInt(values.length)];

        GenerateRandomShip randShip = new GenerateRandomShip();
        ship = randShip.getShip();
        boolean[][] valid = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                valid[i][j] = true;
            }
        }

        Integer[] number = new Integer[12]; //Last one for total components
        for(int i = 0; i < 12; i++){
            number[i] = 0;
        }

        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 12; j++){
                if(valid[i][j]){
                    if(ship.getComponent(i,j) != null){
                        switch (ship.getComponent(i , j).getComponentType()) {
                            case CABIN:
                                assertEquals(ComponentType.CABIN, ship.getComponent(i, j).getComponentType());
                                number[0] += 1;
                                break;
                            case CENTER_CABIN:
                                assertEquals(ComponentType.CENTER_CABIN, ship.getComponent(i, j).getComponentType());
                                number[0] += 1;
                                break;
                            case STORAGE:
                                assertEquals(ComponentType.STORAGE, ship.getComponent(i, j).getComponentType());
                                number[1] += 1;
                                break;
                            case BATTERY:
                                assertEquals(ComponentType.BATTERY, ship.getComponent(i, j).getComponentType());
                                number[2] += 1;
                                break;
                            case SINGLE_ENGINE:
                                assertEquals(ComponentType.SINGLE_ENGINE, ship.getComponent(i, j).getComponentType());
                                number[3] += 1;
                                break;
                            case DOUBLE_ENGINE:
                                assertEquals(ComponentType.DOUBLE_ENGINE, ship.getComponent(i, j).getComponentType());
                                number[4] += 1;
                                break;
                            case SINGLE_CANNON:
                                assertEquals(ComponentType.SINGLE_CANNON, ship.getComponent(i, j).getComponentType());
                                number[5] += 1;
                                break;
                            case DOUBLE_CANNON:
                                assertEquals(ComponentType.DOUBLE_CANNON, ship.getComponent(i, j).getComponentType());
                                number[6] += 1;
                                break;
                            case SHIELD:
                                assertEquals(ComponentType.SHIELD, ship.getComponent(i, j).getComponentType());
                                number[7] += 1;
                                break;
                            case BROWN_LIFE_SUPPORT:
                                assertEquals(ComponentType.BROWN_LIFE_SUPPORT, ship.getComponent(i, j).getComponentType());
                                number[8] += 1;
                                break;
                            case PURPLE_LIFE_SUPPORT:
                                assertEquals(ComponentType.PURPLE_LIFE_SUPPORT, ship.getComponent(i, j).getComponentType());
                                number[9] += 1;
                                break;
                            case CONNECTORS:
                                assertEquals(ComponentType.CONNECTORS, ship.getComponent(i, j).getComponentType());
                                number[10] += 1;
                                break;
                            default:
                                break;
                        }
                        number[11] += 1;
                    }
                }
            }
        }

        assertEquals(number[0], ship.getCabins().size());
        assertEquals(number[1], ship.getStorages().size());
        assertEquals(number[2], ship.getBatteries().size());

        assertEquals(number[3], ship.getSingleEnginesStrength());
        assertEquals(number[4], ship.getDoubleEnginesStrength() / 2);

        assertEquals(number[5] + number[6], ship.getCannons().size());
        assertEquals(number[6], ship.getDoubleCannonsNumber());

        boolean notCabinWithLifeSupport = true;
        boolean check = false;
        for(Map.Entry<Integer, Cabin> c : ship.getCabins().entrySet()){
            ArrayList<Component> surrounding = ship.getSurroundingComponents(c.getValue().getRow(), c.getValue().getColumn());
            for(Component comp : surrounding){
                if(comp != null) {
                    if (comp.getComponentType() == ComponentType.BROWN_LIFE_SUPPORT) {
                        c.getValue().isValid();
                        ship.addCrewMember(c.getValue().getID(), true, false);
                        notCabinWithLifeSupport = false;
                        ship.removeCrewMember(c.getValue().getID(), 1);
                        check = true;
                        break;
                    }
                }
            }
            break;
        }
        if(check){
            assertTrue(number[8] > 0);
        } else {
            assertTrue(notCabinWithLifeSupport);
        }

        notCabinWithLifeSupport = true;
        check = false;
        for(Map.Entry<Integer, Cabin> c : ship.getCabins().entrySet()){
            ArrayList<Component> surrounding = ship.getSurroundingComponents(c.getValue().getRow(), c.getValue().getColumn());
            for(Component comp : surrounding){
                if(comp != null) {
                    if (comp.getComponentType() == ComponentType.PURPLE_LIFE_SUPPORT) {
                        c.getValue().isValid();
                        ship.addCrewMember(c.getValue().getID(), false, true);
                        notCabinWithLifeSupport = false;
                        check = true;
                        break;
                    }
                }
            }
            break;
        }
        if(check){
            assertTrue(number[9] > 0);
        } else {
            assertTrue(notCabinWithLifeSupport);
        }

        int remaining = number[11] - number[0] - number[1] - number[2] - number[3] - number[4] - number[5] - number[6] - number[8] - number[9];
        assertEquals(remaining, number[7] + number[10]); //Number of shields and connectors

        assertEquals(number[11], ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getSurroundingComponents() throws JsonProcessingException {
        Random rand = new Random();
        int count = rand.nextInt(1, 4);

        GenerateRandomShip s = new GenerateRandomShip();
        SpaceShip ship = s.getShip();

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

        for(int i = 1; i < 11; i++){
            for(int j = 1; j < 11; j++){
                if(vs[i][j]){
                    ArrayList<Component> result = new ArrayList<>();
                    result = ship.getSurroundingComponents(i, j);
                    System.out.println(result);
                    assertEquals(ship.getComponent(i - 1, j), result.get(0));
                    assertEquals(ship.getComponent(i, j - 1), result.get(1));
                    assertEquals(ship.getComponent(i + 1, j), result.get(2));
                    assertEquals(ship.getComponent(i, j + 1), result.get(3));
                }
            }
        }
    }

    @RepeatedTest(5)
    void reserveComponent() {
        Component c1, c2, c3;

        c1 = new Storage(0, connectors, true, 2);
        assertDoesNotThrow(() -> ship.reserveComponent(c1));
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(c1, ship.getReservedComponents().getFirst());

        c2 = new Storage(1, connectors, true, 2);
        assertDoesNotThrow(() -> ship.reserveComponent(c2));
        assertEquals(2, ship.getReservedComponents().size());
        assertEquals(c2, ship.getReservedComponents().get(1));

        c3 = new Storage(2, connectors, true, 2);
        assertThrows(IllegalStateException.class, () -> ship.reserveComponent(c3));
    }

    //Return list of all the lost components
    @RepeatedTest(5)
    void getLostComponent() {
        Random rand = new Random();
        List<Component> lostComponents;
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            System.out.println(storage);
            ship.placeComponent(storage, 6, 7 + i);
        }
        ship.destroyComponent(6, 7 + count - 1);
        lostComponents = ship.getLostComponents();
        assertEquals(1, lostComponents.size());
    }

    @RepeatedTest(5)
    void unreserveComponent() {
        Component c1, c2;

        c1 = new Storage(0, connectors, true, 2);
        ship.reserveComponent(c1);
        c2 = new Storage(1, connectors, true, 2);
        ship.reserveComponent(c2);

        ship.unreserveComponent(c1);
        assertEquals(1, ship.getReservedComponents().size());
        assertEquals(c2, ship.getReservedComponents().getFirst());
        ship.unreserveComponent(c2);
        assertEquals(0, ship.getReservedComponents().size());
    }

    @RepeatedTest(5)
    void destroyComponent() throws JsonProcessingException {
        Random rand = new Random();
        int value = 0;
        int number = rand.nextInt(1, 7);
        GenerateRandomShip s = new GenerateRandomShip();
        s.addElementsShip();
        ship = s.getShip();

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

        for(int k = 0; k < number; k++){
            int i, j;
            Component c = null;
            int[] checkValues = new int[8];
            checkValues[0] = ship.getCrewNumber();
            checkValues[1] = ship.getGoodsValue();
            checkValues[2] = ship.getEnergyNumber();
            checkValues[3] = ship.getSingleEnginesStrength();
            checkValues[4] = ship.getDoubleEnginesStrength();
            checkValues[5] = (int) ship.getSingleCannonsStrength();
            checkValues[6] = ship.getDoubleCannonsStrength();
            checkValues[7] = ship.getDoubleCannonsNumber();

            do {
                do {
                    i = rand.nextInt(4, 9);
                    j = rand.nextInt(3, 10);
                } while (!vs[i][j]);

                if (ship.getComponent(i, j) != null) {
                    c = ship.getComponent(i, j);

                    System.out.println("Value goods: " + ship.getGoodsValue());
                    if(c.getComponentType() == ComponentType.STORAGE){
                        value = ((Storage) c).getGoodsValue();
                        System.out.println("Tolgo storage");
                    }
                    ship.destroyComponent(i, j);
                }
            } while (c == null);

            switch (c.getComponentType()) {
                case CABIN, CENTER_CABIN:
                    Cabin c1 = (Cabin) c;
                    if(c1.hasPurpleAlien() || c1.hasBrownAlien()){
                        assertEquals(ship.getCrewNumber() + 1, checkValues[0]);
                    } else if(c1.getCrewNumber() > 0){
                        assertEquals(ship.getCrewNumber() + 2, checkValues[0]);
                    }
                    break;
                case STORAGE:
                    Storage s1 = (Storage) c;
                    System.out.println("Value: " + s1.getGoodsValue());
                    assertEquals(ship.getGoodsValue() + value, checkValues[1]);
                    break;
                case BATTERY:
                    Battery b = (Battery) c;
                    assertEquals(ship.getEnergyNumber() + b.getEnergyNumber(), checkValues[2]);
                    break;
                case SINGLE_ENGINE:
                    Engine e2 = (Engine) c;
                    assertEquals(ship.getSingleEnginesStrength() + e2.getEngineStrength(), checkValues[3]);
                    break;
                case DOUBLE_ENGINE:
                    Engine e1 = (Engine) c;
                    assertEquals(ship.getDoubleEnginesStrength() + e1.getEngineStrength(), checkValues[4]);
                    break;
                case SINGLE_CANNON:
                    Cannon c3 = (Cannon) c;
                    assertEquals(ship.getSingleCannonsStrength() + c3.getCannonStrength(), checkValues[5]);
                    break;
                case DOUBLE_CANNON:
                    Cannon c2 = (Cannon) c;
                    assertEquals(ship.getDoubleCannonsStrength() + c2.getCannonStrength(), checkValues[6]);
                    assertEquals(ship.getDoubleCannonsNumber() + 1, checkValues[7]);
                    break;
                case BROWN_LIFE_SUPPORT:
                    if(ship.getCabins().isEmpty()){
                        assertFalse(ship.hasBrownAlien());
                    } else {
                        for(Cabin cabin : ship.getCabins().values()){
                            if(cabin.hasBrownAlien()){
                                assertTrue(ship.hasBrownAlien());
                            }
                        }
                    }
                    break;
                case PURPLE_LIFE_SUPPORT:
                    if(ship.getCabins().isEmpty()){
                        assertFalse(ship.hasPurpleAlien());
                    } else {
                        for(Cabin cabin : ship.getCabins().values()){
                            if(cabin.hasPurpleAlien()){
                                assertTrue(ship.hasPurpleAlien());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //TODO: fai test con tutti i componenti
    @RepeatedTest(5)
    void placeComponent() {
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            System.out.println(storage);
            ship.placeComponent(storage, 6, 7 + i);
        }
        System.out.println(ship.getNumberOfComponents());
        System.out.println(ship.getStorages().size());
        assertEquals(count, ship.getStorages().size());
    }

    @RepeatedTest(5)
    void getCabin() {
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i + 4, connectors);
            ship.placeComponent(cabin, 6, 7 + i);
        }
        assertEquals(count + 1, ship.getCabins().size());
        for(int i = 0; i < count; i++){
            assertEquals(i + 4, ship.getCabin(i + 4).getID());
            assertEquals(ComponentType.CABIN, ship.getCabin(i + 4).getComponentType());
            assertEquals(ComponentType.CABIN, ship.getCabins().get(i + 4).getComponentType());
        }
        assertThrows((IllegalArgumentException.class), () -> ship.getCabin(100));
    }

    @RepeatedTest(5)
    void getStorage(){
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i + 4, connectors, true, 2);
            ship.placeComponent(storage, 6, 7 + i);
        }
        assertEquals(count, ship.getStorages().size());
        for(int i = 0; i < count + 1; i++){
            if(i == count){
                assertThrows((IllegalArgumentException.class), () -> ship.getStorage(count));
                break;
            }
            assertEquals(i + 4, ship.getStorage(i + 4).getID());
            assertEquals(ComponentType.STORAGE, ship.getStorage(i + 4).getComponentType());
            assertEquals(ComponentType.STORAGE, ship.getStorages().get(i + 4).getComponentType());
        }
    }

    @RepeatedTest(5)
    void getBattery(){
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Battery battery = new Battery(i + 4, connectors, 3);
            ship.placeComponent(battery, 6, 7 + i);
        }
        assertEquals(count, ship.getBatteries().size());
        for(int i = 0; i < count + 1; i++){
            if(i == count){
                assertThrows((IllegalArgumentException.class), () -> ship.getBattery(count));
                break;
            }
            assertEquals(i + 4, ship.getBattery(i + 4).getID());
            assertEquals(ComponentType.BATTERY, ship.getBattery(i + 4).getComponentType());
            assertEquals(ComponentType.BATTERY, ship.getBatteries().get(i + 4).getComponentType());
        }
    }

    //TODO
    @RepeatedTest(5)
    void getDisconnectedComponentsTest(){

    }
}