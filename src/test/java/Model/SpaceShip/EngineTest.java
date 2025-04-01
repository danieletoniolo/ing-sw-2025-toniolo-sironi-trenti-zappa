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

    @RepeatedTest(5)
    void getEngineStrength_withSingleStrength() {
        Engine engine = new Engine(1, connectors, 1);
        assertEquals(1, engine.getEngineStrength());
    }

    @RepeatedTest(5)
    void getEngineStrength_withDoubleStrength() {
        Engine engine = new Engine(1, connectors, 2);
        assertEquals(2, engine.getEngineStrength());
    }

    @RepeatedTest(5)
    void isValid_withNoComponentBelowAndNoRotation() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Engine engine = new Engine(1, connectors, 1);
        ship.placeComponent(engine, 6, 7);
        assertTrue(engine.isValid());
    }

    @RepeatedTest(5)
    void isValid_withComponentBelow() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Engine engine = new Engine(1, connectors, 1);
        Storage storage = new Storage(2, connectors, true, 1);
        ship.placeComponent(engine, 8, 7);
        ship.placeComponent(storage, 9, 7);
        assertFalse(engine.isValid());
    }

    @RepeatedTest(5)
    void isValid_withRotation() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Engine engine = new Engine(1, connectors, 1);
        ship.placeComponent(engine, 6, 7);
        engine.rotateClockwise();
        assertFalse(engine.isValid());
    }

    @RepeatedTest(5)
    void isValid_withComponentBelowAndRotation() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Engine engine = new Engine(1, connectors, 1);
        Storage storage = new Storage(2, connectors, true, 1);
        ship.placeComponent(engine, 8, 7);
        ship.placeComponent(storage, 9, 7);
        engine.rotateClockwise();
        assertFalse(engine.isValid());
    }

    @RepeatedTest(5)
    void getComponentType() {
        Engine engine1 = new Engine(3, connectors, 1);
        assertEquals(ComponentType.SINGLE_ENGINE, engine1.getComponentType());
        Engine engine2 = new Engine(3, connectors, 2);
        assertEquals(ComponentType.DOUBLE_ENGINE, engine2.getComponentType());
    }

    @RepeatedTest(5)
    void getConnection_northFace() {
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Engine(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(0));
    }

    @RepeatedTest(5)
    void getConnection_westFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Engine(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getConnection_southFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Engine(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(2));
    }

    @RepeatedTest(5)
    void getConnection_eastFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE};
        Component component = new Engine(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(3));
    }

    @RepeatedTest(5)
    void getConnection_afterRotation() {
        ConnectorType[] connectors1 = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.DOUBLE, ConnectorType.EMPTY};
        Component component = new Engine(1, connectors1, 1);
        component.rotateClockwise();
        assertEquals(ConnectorType.EMPTY, component.getConnection(0));
        assertEquals(ConnectorType.DOUBLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_initialValue() {
        Component component = new Engine(1, connectors, 1);
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterOneRotation() {
        Component component = new Engine(1, connectors, 1);
        component.rotateClockwise();
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterMultipleRotations() {
        Component component = new Engine(1, connectors, 1);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_fullRotation() {
        Component component = new Engine(1, connectors, 1);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getID_returnsCorrectID() {
        Component component = new Engine(1, connectors, 1);
        assertEquals(1, component.getID());
    }

    @RepeatedTest(5)
    void getID_differentID() {
        Component component = new Engine(2, connectors, 1);
        assertEquals(2, component.getID());
    }

    @RepeatedTest(5)
    void rotateClockwise_once() {
        Component component = new Engine(1, connectors, 1);
        component.rotateClockwise();
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_twice() {
        Component component = new Engine(1, connectors, 1);
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(2, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_threeTimes() {
        Component component = new Engine(1, connectors, 1);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_fourTimes() {
        Component component = new Engine(1, connectors, 1);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_multipleFullRotations() {
        Component component = new Engine(1, connectors, 1);
        for (int i = 0; i < 8; i++) {
            component.rotateClockwise();
        }
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenAttachedToShip() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Component component = new Engine(1, connectors, 1);
        ship.placeComponent(component, 6, 7);
        assertEquals(3, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenNotAttachedToShip_throwsException() {
        Component component = new Engine(1, connectors, 1);
        assertThrows(IllegalStateException.class, component::getExposedConnectors);
    }

    @RepeatedTest(5)
    void getExposedConnectors_withSurroundingComponents() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Component component = new Engine(1, connectors, 1);
        Component adjacentComponent = new Engine(2, connectors, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertEquals(2, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void isConnected_withAdjacentComponent() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Component component = new Engine(1, connectors, 1);
        Component adjacentComponent = new Engine(2, connectors, 1);
        ship.placeComponent(component, 6, 7);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isConnected_withMultipleAdjacentComponents() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        Component component = new Engine(1, connectors, 1);
        Component adjacentComponent1 = new Engine(2, connectors, 1);
        Component adjacentComponent2 = new Engine(3, connectors, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isFixed_initiallyFalse() {
        Component component = new Engine(1, connectors, 1);
        assertFalse(component.isFixed());
    }

    @RepeatedTest(5)
    void isFixed_afterFixing() {
        Component component = new Engine(1, connectors, 1);
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void isFixed_afterMultipleFixCalls() {
        Component component = new Engine(1, connectors, 1);
        component.fix();
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void fix_setsFixedToTrue() {
        Component component = new Engine(1, connectors, 1);
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void fix_doesNotChangeFixedStateIfAlreadyFixed() {
        Component component = new Engine(1, connectors, 1);
        component.fix();
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void isValid_withAllValidConnections() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Engine(1, connectors, 1);
        Component adjacentComponent = new Engine(2, connectors, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withInvalidConnections() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Engine(1, connectors, 1);
        Component adjacentComponent = new Engine(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertFalse(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withTripleConnector() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        ConnectorType[] connectors = {ConnectorType.TRIPLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Engine(1, connectors, 1);
        Component adjacentComponent = new Engine(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.TRIPLE, ConnectorType.SINGLE}, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withMixedConnections() {
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Engine(1, connectors, 1);
        Component adjacentComponent1 = new Engine(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 1);
        Component adjacentComponent2 = new Engine(3, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 6, 6);
        assertFalse(component.isValid());
    }

/*
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

    //TODO: Fare prima isValid di engine
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

        Engine engine1 = new Engine(1, connectors1, 1);
        Engine engine2 = new Engine(2, connectors2, 2);

        ship.placeComponent(engine1, 6, 7);
        System.out.println(engine1.isValid());

        if(engine1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(engine1.isValid());
        } else {
            assertFalse(engine1.isValid());
            return;
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
 */
}