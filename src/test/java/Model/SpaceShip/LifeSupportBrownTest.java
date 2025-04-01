package Model.SpaceShip;

import Model.Game.Board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class LifeSupportBrownTest {
    LifeSupportBrown lsb;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        lsb = new LifeSupportBrown(0, connectors);
        assertNotNull(lsb, "Component not initialized correctly");
    }

    @RepeatedTest(5)
    void getComponentType_returnsBrownLifeSupport() {
        LifeSupportBrown lsb = new LifeSupportBrown(1, connectors);
        assertEquals(ComponentType.BROWN_LIFE_SUPPORT, lsb.getComponentType());
    }

    @RepeatedTest(5)
    void getConnection_northFace() {
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new LifeSupportBrown(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(0));
    }

    @RepeatedTest(5)
    void getConnection_westFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new LifeSupportBrown(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getConnection_southFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new LifeSupportBrown(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(2));
    }

    @RepeatedTest(5)
    void getConnection_eastFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE};
        Component component = new LifeSupportBrown(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(3));
    }

    @RepeatedTest(5)
    void getConnection_afterRotation() {
        ConnectorType[] connectors1 = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.DOUBLE, ConnectorType.EMPTY};
        Component component = new LifeSupportBrown(1, connectors1);
        component.rotateClockwise();
        assertEquals(ConnectorType.EMPTY, component.getConnection(0));
        assertEquals(ConnectorType.DOUBLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_initialValue() {
        Component component = new LifeSupportBrown(1, connectors);
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterOneRotation() {
        Component component = new LifeSupportBrown(1, connectors);
        component.rotateClockwise();
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterMultipleRotations() {
        Component component = new LifeSupportBrown(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_fullRotation() {
        Component component = new LifeSupportBrown(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getID_returnsCorrectID() {
        Component component = new LifeSupportBrown(1, connectors);
        assertEquals(1, component.getID());
    }

    @RepeatedTest(5)
    void getID_differentID() {
        Component component = new LifeSupportBrown(2, connectors);
        assertEquals(2, component.getID());
    }

    @RepeatedTest(5)
    void rotateClockwise_once() {
        Component component = new LifeSupportBrown(1, connectors);
        component.rotateClockwise();
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_twice() {
        Component component = new LifeSupportBrown(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(2, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_threeTimes() {
        Component component = new LifeSupportBrown(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_fourTimes() {
        Component component = new LifeSupportBrown(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_multipleFullRotations() {
        Component component = new LifeSupportBrown(1, connectors);
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
        Component component = new LifeSupportBrown(1, connectors);
        ship.placeComponent(component, 6, 7);
        assertEquals(3, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenNotAttachedToShip_throwsException() {
        Component component = new LifeSupportBrown(1, connectors);
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
        Component component = new LifeSupportBrown(1, connectors);
        Component adjacentComponent = new LifeSupportBrown(2, connectors);
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
        Component component = new LifeSupportBrown(1, connectors);
        Component adjacentComponent = new LifeSupportBrown(2, connectors);
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
        Component component = new LifeSupportBrown(1, connectors);
        Component adjacentComponent1 = new LifeSupportBrown(2, connectors);
        Component adjacentComponent2 = new LifeSupportBrown(3, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isFixed_initiallyFalse() {
        Component component = new LifeSupportBrown(1, connectors);
        assertFalse(component.isFixed());
    }

    @RepeatedTest(5)
    void isFixed_afterFixing() {
        Component component = new LifeSupportBrown(1, connectors);
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void isFixed_afterMultipleFixCalls() {
        Component component = new LifeSupportBrown(1, connectors);
        component.fix();
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void fix_setsFixedToTrue() {
        Component component = new LifeSupportBrown(1, connectors);
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void fix_doesNotChangeFixedStateIfAlreadyFixed() {
        Component component = new LifeSupportBrown(1, connectors);
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
        Component component = new LifeSupportBrown(1, connectors);
        Component adjacentComponent = new LifeSupportBrown(2, connectors);
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
        Component component = new LifeSupportBrown(1, connectors);
        Component adjacentComponent = new LifeSupportBrown(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
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
        Component component = new LifeSupportBrown(1, connectors);
        Component adjacentComponent = new LifeSupportBrown(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.TRIPLE, ConnectorType.SINGLE});
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
        Component component = new LifeSupportBrown(1, connectors);
        Component adjacentComponent1 = new LifeSupportBrown(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        Component adjacentComponent2 = new LifeSupportBrown(3, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 6, 6);
        assertFalse(component.isValid());
    }




/*
    @RepeatedTest(10)
    void getComponentType() {
        lsb = new LifeSupportBrown(0, connectors);
        ComponentType type = lsb.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.BROWN_LIFE_SUPPORT, type);
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, lsb.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        LifeSupportBrown l = new LifeSupportBrown(id, connectors);

        assertEquals(id, l.getID());
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

        LifeSupportBrown l = new LifeSupportBrown(1, connectorArray);

        for(int k = 0; k < 4; k++){
            assertEquals(l.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, lsb.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            lsb.rotateClockwise();
        }
        assertEquals(r, lsb.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = lsb.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            lsb.rotateClockwise();
        }

        int b = lsb.getClockwiseRotation();
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

        LifeSupportBrown l = new LifeSupportBrown(1, connectorArray);
        ship.placeComponent(l, 6, 7);
        if(l.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, l.getExposedConnectors());

        LifeSupportBrown l1 = new LifeSupportBrown(2, connectorArray);
        ship.placeComponent(l1, 6, 8);
        if(l.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, l.getExposedConnectors());
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

            LifeSupportBrown l = new LifeSupportBrown(1, connector);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                l.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(l, 6, 7 + j);

            assertTrue(l.isConnected(6, 7 + j));
        }
    }

    //TODO: finire il metodo quando implementano il metodo di spostare i componenti
    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(lsb.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        LifeSupportBrown[] lsbs = new LifeSupportBrown[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            lsbs[j] = new LifeSupportBrown(j, connectors);
            System.out.println(lsbs[j]);

            ship.placeComponent(lsbs[j], 6, 7 + j);

            if(j > 0){
                lsbs[i].fix();
                assertTrue(lsbs[i].isFixed());
                i++;
            }

            assertFalse(lsbs[j].isFixed());
        }
    }

    //TODO: controllo isValid e sua funzione
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

        LifeSupportBrown lsb1 = new LifeSupportBrown(1, connectors1);
        LifeSupportBrown lsb2 = new LifeSupportBrown(2, connectors2);

        ship.placeComponent(lsb1, 6, 7);
        System.out.println(lsb1.isValid());

        if(lsb1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(lsb1.isValid());
        } else {
            assertFalse(lsb1.isValid());
            return;
        }

        ship.placeComponent(lsb2, 6, 8);
        System.out.println(lsb1.isValid());

        if((lsb1.getConnection(3) == ConnectorType.EMPTY && lsb2.getConnection(1) != ConnectorType.EMPTY) ||
                (lsb1.getConnection(3) == ConnectorType.SINGLE && lsb2.getConnection(1) != ConnectorType.SINGLE &&
                        lsb2.getConnection(1) != ConnectorType.TRIPLE) ||
                (lsb1.getConnection(3) == ConnectorType.DOUBLE && lsb2.getConnection(1) != ConnectorType.DOUBLE &&
                        lsb2.getConnection(1) != ConnectorType.TRIPLE) ||
                (lsb1.getConnection(3) == ConnectorType.TRIPLE && lsb2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(lsb1.isValid());
        } else {
            assertTrue(lsb1.isValid());
        }
    }

 */
}