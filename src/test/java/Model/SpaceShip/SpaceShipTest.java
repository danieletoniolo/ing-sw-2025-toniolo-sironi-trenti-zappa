package Model.SpaceShip;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
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
        assertEquals(0, ship.getNumberOfComponents());

        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            ship.placeComponent(storage, 6, 7 + i);
        }
        assertEquals(count, ship.getNumberOfComponents());
    }

    @RepeatedTest(5)
    void getSingleEnginesStrength() {
        assertEquals(0, ship.getSingleEnginesStrength());

        Random rand = new Random();
        int j = 0;
        int i = 0, power = 0;
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
        int j = 0;
        int i = 0, power = 0;
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
        int j = 0;
        int i = 0, power = 0;
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
        int j = 0;
        int i = 0;
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
        int j = 0;
        int i = 0, power = 0;
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
        int j = 0;
        int i = 0, power = 0;
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
        int j = 0;
        int i = 0, power = 0;
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

    //Test getGoodsValue and refreshGoodsValue
    @RepeatedTest(5)
    void getGoodsValue() {
        assertEquals(0, ship.getGoodsValue());

        Random rand = new Random();
        int limit = rand.nextInt(4);
        int index = 0;
        int total = 0;
        for(int i = 0; i < limit; i++){
            int j = rand.nextInt(1,4);
            boolean bool = rand.nextBoolean();
            Storage storage = new Storage(i, connectors, bool, j);

            int r = rand.nextInt(4) + 1;
            for(int k = 0; k < r; k++){
                storage.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(storage, 6,7 + index);
            index++;

            for(int k = 0; k < j; k++){
                GoodType[] values = GoodType.values();
                GoodType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                Good good = new Good(randomType);

                if(storage.isDangerous()){
                    storage.addGood(good);
                    total += good.getValue();
                } else {
                    if(good.getColor() != GoodType.RED){
                        storage.addGood(good);
                        total += good.getValue();
                    } else {
                        System.out.println("Red good not added");
                        // k--; If we want to add another good
                    }
                }
            }
            ship.refreshGoodsValue();
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
                    ship.getCabin(i + 4).addBrownLifeSupport();
                    ship.addCrewMember(cabin.getID(), true, false);
                    total += 1;
                    System.out.println("1");

                    assertTrue(ship.hasBrownAlien());
                    assertEquals(1, cabin.getCrewNumber());
                } else {
                    ship.getCabin(i + 4).addPurpleLifeSupport();
                    ship.addCrewMember(cabin.getID(), false, true);
                    total += 1;
                    System.out.println("2");

                    assertTrue(ship.hasPurpleAlien());
                    assertEquals(1, cabin.getCrewNumber());
                }
            } else {
                ship.addCrewMember(cabin.getID(), false, false);
                total += 2;
                System.out.println("3");
                assertEquals(2, cabin.getCrewNumber());
            }
        }

        System.out.println(total);
        assertEquals(total, ship.getCrewNumber());

        for(int j = 0; j < limit; j++){
            boolean flag = rand.nextBoolean();
            System.out.println("ID: " + (j));
            if(flag){ //Voglio rimuovere
                if (ship.getCabin(j + 4).hasBrownAlien()) {
                    System.out.println("1.1");

                    ship.removeCrewMember(j + 4, 1);
                    assertFalse(ship.hasBrownAlien());
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 1;
                } else if(ship.getCabin(j + 4).hasPurpleAlien()) {
                    System.out.println("2.1");

                    ship.removeCrewMember(j + 4, 1);
                    assertFalse(ship.hasPurpleAlien());
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 1;
                } else {
                    System.out.println("3.1");

                    ship.removeCrewMember(j + 4, 2);
                    assertEquals(0, ship.getCabin(j + 4).getCrewNumber());

                    total -= 2;
                }
            }
        }

        assertEquals(total, ship.getCrewNumber());
    }

    //TODO: DA RIFARE
    @RepeatedTest(5)
    void canProtect() {
        Random rand = new Random();
        int dice = rand.nextInt(7,10);
        int number = rand.nextInt(1, 5);
        Pair<Component, Integer> pair = new Pair<>(null, 0);

        Shield shield = new Shield(0, connectors);
        for(int j = 0; j < rand.nextInt(4); j++){
            shield.rotateClockwise();
        }
        System.out.println("Rotation shield: " + shield.getClockwiseRotation());
        ship.placeComponent(shield, 6,7);

        Battery battery = new Battery(1, connectors, 3);
        for(int j = 0; j < rand.nextInt(4); j++){
            battery.rotateClockwise();
        }
        ship.placeComponent(battery, 6,8);

        Cannon cannon = new Cannon(0, connectors, 1);
        for(int j = 0; j < rand.nextInt(4); j++){
            shield.rotateClockwise();
        }
        System.out.println("Rotation cannon: " + cannon.getClockwiseRotation());
        ship.placeComponent(cannon, 6,9);

        for(int i = 0; i < number; i++){
            HitType[] values = HitType.values();
            HitType randomType = values[rand.nextInt(values.length)];
            System.out.println(randomType);

            Direction[] direction = Direction.values();
            Direction randomDirection = direction[rand.nextInt(direction.length)];
            System.out.println(randomDirection);

            Hit hit = new Hit(randomType, randomDirection);

            pair = ship.canProtect(dice, hit);

            System.out.println(pair);

            if(pair.getValue0() != null){ //Ho colpito qualcosa
                if(pair.getValue1() == 1){
                    //TODO: ABBIAMO UN METODO CHE CONTROLLA CHE IL SINGOLO COMPONENTE NON ABBIA CONNETTORI ESPOSTI IN QUELLA DIREZIONE
                    System.out.println("I'm protected");
                    assertEquals(HitType.SMALLMETEOR, hit.getType());
                } else if(pair.getValue1() == 0){

                    System.out.println("I use a battery for protection");
                    assertNotEquals(HitType.HEAVYFIRE, hit.getType());
                } else {
                    System.out.println("I don't have any chance to protect me");
                    if(!shield.canShield(hit.getDirection().getValue()) && hit.getType() != HitType.HEAVYFIRE){
                        assertNotEquals(HitType.HEAVYFIRE, hit.getType());
                    } else {
                        assertEquals(HitType.HEAVYFIRE, hit.getType());
                    }
                }
            }
        }
    }

    //TODO: DA FINIRE pk non hanno finito nel model
    @RepeatedTest(5)
    void useEnergy() {
        Random rand = new Random();
        Map<Integer, Battery> batteries = new HashMap<>();
        int count = rand.nextInt(3) + 1;
        for(int i = 0; i < count; i++){
            int energy = rand.nextInt(0, 4);
            System.out.println("Battery " + i + " : " + energy);
            Battery battery = new Battery(i, connectors, energy);
            for(int j = 0; j < rand.nextInt(4); j++){
                battery.rotateClockwise();
            }
            ship.placeComponent(battery, 6,7 + i);
        }

        batteries = ship.getBatteries();

        for(int i = 0; i < count; i++){
            Battery batterySingle;
            batterySingle = batteries.get(i);
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

    //TODO: DA CAPIRE SE FUNZIONA CORRETTAMENTE. CHIEDERE IL GIRO DEI CONNECTORS pk mi sa che uno gira in senso orario e l'altro in senso antiorario (alcuni test errati)
    //TODO: Non tornano i risultati
    //Giro ConnectorType senso antiorario
    //getInvalidComponents restituisce ENTRAMBI i pezzi della ship con connettori non validi
    @RepeatedTest(5)
    void getInvalidComponents() {
        Random rand = new Random();
        ConnectorType[] values = ConnectorType.values();
        List<Pair<Integer, Integer>> invalidComponents = new ArrayList<>();
        ConnectorType[] connector = new ConnectorType[4];
        boolean shipOk = true;

        int count = rand.nextInt(1, 4);
        for(int j = 0; j < count; j++){
            for(int i = 0; i < 4; i++){
                ConnectorType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                connector[i] = randomType;
            }

            Storage storage = new Storage(j, connector, true, 2);
            System.out.println(storage);
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

    @RepeatedTest(5)
    void getComponent() {
        Random rand = new Random();
        ComponentType[] values = ComponentType.values();
        ComponentType randomType = values[rand.nextInt(values.length)];
        System.out.println(randomType);

        //TODO: CHIEDERE AI BOYS SE VOGLIAMO FARE UN TEST CHE FA CLASSI RANDOM, MA SERVE UNA INTERFACCIA (CHATGPT)
    }

    @RepeatedTest(5)
    void getSurroundingComponents() {
        Random rand = new Random();
        int count = rand.nextInt(1, 4);

        //TODO: CHIEDI QUELLO CHE HAI SCRITTO SOPRA
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
        List<Component> lostComponents = new ArrayList<>();
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

    //TODO: DA FARE TUTTI I CASI PER OGNI SINGOLO COMPONENTE - Fare classe random?
    @RepeatedTest(5)
    void destroyComponent() {
        int number = ship.getNumberOfComponents();

        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            ship.placeComponent(storage, 6, 7 + i);
        }
        for(int i = 0; i < count; i++){
            ship.destroyComponent(6, 7 + count - i - 1);
            assertEquals(count - i - 1, ship.getNumberOfComponents());
        }
    }

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
            Cabin cabin = new Cabin(i, connectors);
            ship.placeComponent(cabin, 6, 7 + i);
        }
        assertEquals(count, ship.getCabins().size());
        for(int i = 0; i < count; i++){
            assertEquals(i, ship.getCabin(i).getID());
            assertEquals(ComponentType.CABIN, ship.getCabin(i).getComponentType());
            assertEquals(ComponentType.CABIN, ship.getCabins().get(i).getComponentType());

        }
    }

    @RepeatedTest(5)
    void getStorage(){
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Storage storage = new Storage(i, connectors, true, 2);
            ship.placeComponent(storage, 6, 7 + i);
        }
        assertEquals(count, ship.getStorages().size());
        for(int i = 0; i < count; i++){
            assertEquals(i, ship.getStorage(i).getID());
            assertEquals(ComponentType.STORAGE, ship.getStorage(i).getComponentType());
            assertEquals(ComponentType.STORAGE, ship.getStorages().get(i).getComponentType());
        }
    }

    @RepeatedTest(5)
    void getBattery(){
        Random rand = new Random();
        int count = rand.nextInt(1, 4);
        for(int i = 0; i < count; i++){
            Battery battery = new Battery(i, connectors, 3);
            ship.placeComponent(battery, 6, 7 + i);
        }
        assertEquals(count, ship.getBatteries().size());
        for(int i = 0; i < count; i++){
            assertEquals(i, ship.getBattery(i).getID());
            assertEquals(ComponentType.BATTERY, ship.getBattery(i).getComponentType());
            assertEquals(ComponentType.BATTERY, ship.getBatteries().get(i).getComponentType());
        }
    }

    //TODO
    @RepeatedTest(5)
    void getDisconnectedComponentsTest(){

    }
}