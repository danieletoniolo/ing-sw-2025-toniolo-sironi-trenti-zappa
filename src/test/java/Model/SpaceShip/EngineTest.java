package Model.SpaceShip;

import Model.Game.Board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {
    Engine engine;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        engine = new Engine(0, connectors, 1);
        assertNotNull(engine, "Component not initialized correctly");
    }

    @RepeatedTest(10)
    void getComponentType() {
        Random rand = new Random();
        int i = rand.nextInt(2) + 1;

        engine = new Engine(0, connectors, i);
        ComponentType type = engine.getComponentType();
        System.out.println(type);

        if(type == ComponentType.SINGLE_ENGINE) {
            assertEquals(1, i);
        } else {
            assertEquals(2, i);
        }
    }

    @RepeatedTest(5)
    void getRowTest(){
        assertEquals(6, engine.getRow());

        Random rand = new Random();
        int r = rand.nextInt(4,9);
        Engine engine = new Engine(1, connectors, 1);

        assertEquals(r, engine.getRow());
    }

    @RepeatedTest(5)
    void getColumnTest(){
        assertEquals(7, engine.getColumn());

        Random rand = new Random();
        int c = rand.nextInt(5,9);
        Engine engine = new Engine(1, connectors, 1);

        assertEquals(c, engine.getColumn());
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, engine.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Engine engine = new Engine(id, connectors, 1);

        assertEquals(id, engine.getID());
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

        Engine engine = new Engine(1, connectorArray, 1);

        for(int k = 0; k < 4; k++){
            assertEquals(engine.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, engine.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            engine.rotateClockwise();
        }
        assertEquals(r, engine.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = engine.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            engine.rotateClockwise();
        }

        int b = engine.getClockwiseRotation();
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

        Engine engine = new Engine(1, connectorArray, 1);
        ship.placeComponent(engine, 6, 7);
        if(engine.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, engine.getExposedConnectors());

        Engine engine1 = new Engine(2, connectorArray, 2);
        ship.placeComponent(engine1, 6, 8);
        if(engine.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, engine.getExposedConnectors());
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

            Engine engine = new Engine(1, connector, 1);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                engine.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(engine, 6, 7 + j);

            assertTrue(engine.isConnected(6, 7 + j));
        }
    }

    //TODO: finire il metodo quando implementano il metodo di spostare i componenti
    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(engine.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Engine[] es = new Engine[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            es[j] = new Engine(j, connectors, 2);
            System.out.println(es[j]);

            ship.placeComponent(es[j], 6, 7 + j);

            if(j > 0){
                es[i].fix();
                assertTrue(es[i].isFixed());
                i++;
            }

            assertFalse(es[j].isFixed());
        }
    }

    //TODO: Fare prima isValid di cannon
    @RepeatedTest(5)
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

        Engine engine1 = new Engine(1, connectors1, 1);
        Engine engine2 = new Engine(2, connectors2, 2);

        ship.placeComponent(engine1, 6, 7);
        System.out.println(engine1.isValid());

        if(engine1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(engine1.isValid());
        } else {
            assertFalse(engine1.isValid());
        }

        ship.placeComponent(engine2, 6, 8);
        System.out.println(engine1.isValid());

        if((engine1.getConnection(3) == ConnectorType.EMPTY && engine2.getConnection(1) != ConnectorType.EMPTY) ||
                (engine1.getConnection(3) == ConnectorType.SINGLE && engine2.getConnection(1) != ConnectorType.SINGLE &&
                        engine2.getConnection(1) != ConnectorType.TRIPLE) ||
                (engine1.getConnection(3) == ConnectorType.DOUBLE && engine2.getConnection(1) != ConnectorType.DOUBLE &&
                        engine2.getConnection(1) != ConnectorType.TRIPLE) ||
                (engine1.getConnection(3) == ConnectorType.TRIPLE && engine2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(engine1.isValid());
        } else {
            assertTrue(engine1.isValid());
        }
    }

    @RepeatedTest(10)
    void getEngineStrength() {
        assertEquals(1, engine.getEngineStrength());

        Random rand = new Random();
        int power = rand.nextInt(2) + 1;
        System.out.println(power);
        Engine engine = new Engine(1, connectors, power);
        assertEquals(power, engine.getEngineStrength());
    }
}