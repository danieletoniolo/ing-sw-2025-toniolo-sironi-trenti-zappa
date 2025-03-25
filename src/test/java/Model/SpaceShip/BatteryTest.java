package Model.SpaceShip;

import Model.Game.Board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BatteryTest {
    Battery b;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        b = new Battery(0, connectors, 1);
        assertNotNull(b, "Component not initialized correctly");
    }

    @RepeatedTest(10)
    void getComponentType() {
        Random rand = new Random();
        int i = rand.nextInt(2) + 1;

        b = new Battery(0, connectors, i);
        ComponentType type = b.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.BATTERY, type);
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, b.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Battery battery = new Battery(id, connectors, 1);

        assertEquals(id, battery.getID());
    }

    @RepeatedTest(5)
    void getConnectionTest(){
        Random rand = new Random();
        ConnectorType[] values = ConnectorType.values();
        ConnectorType[] connectorArray = new ConnectorType[4];
        ConnectorType[] check = new ConnectorType[4];
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            System.out.println(randomType);
            connectorArray[i] = randomType;
            check[i] = randomType;
        }

        Battery battery = new Battery(1, connectorArray, 1);

        for(int k = 0; k < 4; k++){
            assertEquals(battery.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, b.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            b.rotateClockwise();
        }
        assertEquals(r, b.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = b.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            b.rotateClockwise();
        }

        int c = b.getClockwiseRotation();
        assertEquals(a, c);
    }

    @RepeatedTest(5)
    void getExposedConnectorsTest(){
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        ConnectorType[] values = ConnectorType.values();
        ConnectorType[] connectorArray = new ConnectorType[4];
        int exposed = 0;
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            System.out.println(randomType);
            connectorArray[i] = randomType;
            if(randomType != ConnectorType.EMPTY){
                exposed++;
            }
        }

        Battery battery = new Battery(1, connectorArray, 1);
        ship.placeComponent(battery, 6, 7);
        if(battery.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, battery.getExposedConnectors());

        Battery battery1 = new Battery(2, connectorArray, 1);
        ship.placeComponent(battery1, 6, 8);
        if(battery.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, battery.getExposedConnectors());
    }

    @RepeatedTest(5)
    void isConnectedTest(){
        SpaceShip ship;
        boolean[][] spots;
        spots = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                spots[i][j] = true;
            }
        }
        ship = new SpaceShip(Level.SECOND, spots);

        Random rand = new Random();
        int i = rand.nextInt(5);
        for(int j = 0; j < i; j++) {
            ConnectorType[] values = ConnectorType.values();
            ConnectorType[] connector = new ConnectorType[4];
            for(int k = 0; k < 4; k++){
                ConnectorType randomType = values[rand.nextInt(values.length)];
                System.out.println(randomType);
                connector[k] = randomType;
            }

            Battery battery = new Battery(1, connector, 1);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                battery.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(battery, 6, 7 + j);

            assertTrue(battery.isConnected(6, 7 + j));
        }
    }

    //TODO: finire il metodo quando implementano il metodo di spostare i componenti
    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(b.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Battery[] bs = new Battery[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            bs[j] = new Battery(j, connectors, 2);
            System.out.println(bs[j]);

            ship.placeComponent(bs[j], 6, 7 + j);

            if(j > 0){
                bs[i].fix();
                assertTrue(bs[i].isFixed());
                i++;
            }

            assertFalse(bs[j].isFixed());
        }
    }

    @RepeatedTest(500)
    void isValidTest() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);

        Random rand = new Random();
        ConnectorType[] values = ConnectorType.values();
        ConnectorType[] connectors1 = new ConnectorType[4];
        ConnectorType[] connectors2 = new ConnectorType[4];
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            connectors1[i] = randomType;
            System.out.println(randomType);
        }
        System.out.println();
        for(int i = 0; i < 4; i++){
            ConnectorType randomType = values[rand.nextInt(values.length)];
            connectors2[i] = randomType;
            System.out.println(randomType);
        }

        Battery battery = new Battery(1, connectors1, 1);
        Battery battery1 = new Battery(2, connectors2, 1);

        ship.placeComponent(battery, 6, 7);
        System.out.println(battery.isValid());

        if(battery.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(battery.isValid());
        } else {
            assertFalse(battery.isValid());
            return;
        }

        ship.placeComponent(battery1, 6, 8);
        System.out.println(battery.isValid());

        if((battery.getConnection(3) == ConnectorType.EMPTY && battery1.getConnection(1) != ConnectorType.EMPTY) ||
                (battery.getConnection(3) == ConnectorType.SINGLE && battery1.getConnection(1) != ConnectorType.SINGLE &&
                        battery1.getConnection(1) != ConnectorType.TRIPLE) ||
                (battery.getConnection(3) == ConnectorType.DOUBLE && battery1.getConnection(1) != ConnectorType.DOUBLE &&
                        battery1.getConnection(1) != ConnectorType.TRIPLE) ||
                (battery.getConnection(3) == ConnectorType.TRIPLE && battery1.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(battery.isValid());
        } else {
            assertTrue(battery.isValid());
        }
    }

    @RepeatedTest(5)
    void getEnergyNumber() {
        Random rand = new Random();
        int i = rand.nextInt(5) + 1;

        Battery battery = new Battery(1, connectors, i);
        assertEquals(i, battery.getEnergyNumber());
    }

    @RepeatedTest(5)
    void removeEnergy() {
        Random rand = new Random();
        int i = rand.nextInt(5) + 1;

        Battery battery = new Battery(1, connectors, i);
        for(int j = 0; j < i; j++){
            System.out.println(battery.getEnergyNumber());
            battery.removeEnergy();
            assertEquals(i - j - 1, battery.getEnergyNumber());
        }
        assertThrows(IllegalStateException.class, battery::removeEnergy);
    }
}