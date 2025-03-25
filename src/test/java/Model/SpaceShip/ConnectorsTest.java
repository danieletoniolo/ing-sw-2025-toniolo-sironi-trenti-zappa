package Model.SpaceShip;

import Model.Game.Board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorsTest {
    Connectors c;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        c = new Connectors(0, connectors);
        assertNotNull(c, "Component not initialized correctly");
    }

    @RepeatedTest(10)
    void getComponentType() {
        c = new Connectors(0, connectors);
        ComponentType type = c.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.CONNECTORS, type);
    }

    @RepeatedTest(5)
    void getRowTest(){
        assertEquals(6, c.getRow());

        Random rand = new Random();
        int r = rand.nextInt(4,9);
        Connectors connector = new Connectors(1, connectors);

        assertEquals(r, connector.getRow());
    }

    @RepeatedTest(5)
    void getColumnTest(){
        assertEquals(7, c.getColumn());

        Random rand = new Random();
        int c = rand.nextInt(5,9);
        Connectors connector = new Connectors(1, connectors);

        assertEquals(c, connector.getColumn());
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, c.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Connectors connector = new Connectors(id, connectors);

        assertEquals(id, connector.getID());
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

        Connectors connector = new Connectors(1, connectorArray);

        for(int k = 0; k < 4; k++){
            assertEquals(connector.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, c.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            c.rotateClockwise();
        }
        assertEquals(r, c.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = c.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            c.rotateClockwise();
        }

        int b = c.getClockwiseRotation();
        assertEquals(a, b);
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

        Connectors connector = new Connectors(1, connectorArray);
        ship.placeComponent(connector, 6, 7);
        if(connector.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, connector.getExposedConnectors());

        Connectors connector1 = new Connectors(2, connectorArray);
        ship.placeComponent(connector1, 6, 8);
        if(connector.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, connector.getExposedConnectors());
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

            Connectors connectors = new Connectors(1, connector);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                connectors.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(connectors, 6, 7 + j);

            assertTrue(connectors.isConnected(6, 7 + j));
        }
    }

    //TODO: finire il metodo quando implementano il metodo di spostare i componenti
    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(c.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Connectors[] cs = new Connectors[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            cs[j] = new Connectors(j, connectors);
            System.out.println(cs[j]);

            ship.placeComponent(cs[j], 6, 7 + j);

            if(j > 0){
                cs[i].fix();
                assertTrue(cs[i].isFixed());
                i++;
            }

            assertFalse(cs[j].isFixed());
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

        Connectors connector1 = new Connectors(1, connectors1);
        Connectors connector2 = new Connectors(2, connectors2);

        ship.placeComponent(connector1, 6, 7);
        System.out.println(connector1.isValid());

        if(connector1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(connector1.isValid());
        } else {
            assertFalse(connector1.isValid());
            return;
        }

        ship.placeComponent(connector2, 6, 8);
        System.out.println(connector1.isValid());

        if((connector1.getConnection(3) == ConnectorType.EMPTY && connector2.getConnection(1) != ConnectorType.EMPTY) ||
                (connector1.getConnection(3) == ConnectorType.SINGLE && connector2.getConnection(1) != ConnectorType.SINGLE &&
                        connector2.getConnection(1) != ConnectorType.TRIPLE) ||
                (connector1.getConnection(3) == ConnectorType.DOUBLE && connector2.getConnection(1) != ConnectorType.DOUBLE &&
                        connector2.getConnection(1) != ConnectorType.TRIPLE) ||
                (connector1.getConnection(3) == ConnectorType.TRIPLE && connector2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(connector1.isValid());
        } else {
            assertTrue(connector1.isValid());
        }
    }
}