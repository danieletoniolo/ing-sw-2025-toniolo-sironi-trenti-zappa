package Model.SpaceShip;

import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShieldTest {
    Shield s;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        s = new Shield(0, connectors);
        assertNotNull(s);
    }

    @Test
    void testShieldConstructor() {
        Shield a = new Shield();
        assertNotNull(a);
        assertEquals(0, a.getID());
    }

    @RepeatedTest(5)
    void canShield_withoutRotation() {
        Shield shield = new Shield(1, connectors);
        assertTrue(shield.canShield(0));
        assertFalse(shield.canShield(1));
        assertFalse(shield.canShield(2));
        assertTrue(shield.canShield(3));
    }

    @RepeatedTest(5)
    void canShield_afterSingleRotation() {
        Shield shield = new Shield(1, connectors);
        shield.rotateClockwise();
        assertFalse(shield.canShield(0));
        assertFalse(shield.canShield(1));
        assertTrue(shield.canShield(2));
        assertTrue(shield.canShield(3));
    }

    @RepeatedTest(5)
    void canShield_afterDoubleRotation() {
        Shield shield = new Shield(1, connectors);
        shield.rotateClockwise();
        shield.rotateClockwise();
        assertFalse(shield.canShield(0));
        assertTrue(shield.canShield(1));
        assertTrue(shield.canShield(2));
        assertFalse(shield.canShield(3));
    }

    @RepeatedTest(5)
    void canShield_afterTripleRotation() {
        Shield shield = new Shield(1, connectors);
        shield.rotateClockwise();
        shield.rotateClockwise();
        shield.rotateClockwise();
        assertTrue(shield.canShield(0));
        assertTrue(shield.canShield(1));
        assertFalse(shield.canShield(2));
        assertFalse(shield.canShield(3));
    }

    @RepeatedTest(5)
    void getComponentType_returnsShield() {
        Shield shield = new Shield(1, connectors);
        assertEquals(ComponentType.SHIELD, shield.getComponentType());
    }

    @RepeatedTest(5)
    void getConnection_northFace() {
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Shield(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(0));
    }

    @RepeatedTest(5)
    void getConnection_westFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Shield(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getConnection_southFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Shield(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(2));
    }

    @RepeatedTest(5)
    void getConnection_eastFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE};
        Component component = new Shield(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(3));
    }

    @RepeatedTest(5)
    void getConnection_afterRotation() {
        ConnectorType[] connectors1 = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.DOUBLE, ConnectorType.EMPTY};
        Component component = new Shield(1, connectors1);
        component.rotateClockwise();
        assertEquals(ConnectorType.EMPTY, component.getConnection(0));
        assertEquals(ConnectorType.DOUBLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_initialValue() {
        Component component = new Shield(1, connectors);
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterOneRotation() {
        Component component = new Shield(1, connectors);
        component.rotateClockwise();
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterMultipleRotations() {
        Component component = new Shield(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getClockwiseRotation_fullRotation() {
        Component component = new Shield(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getID_returnsCorrectID() {
        Component component = new Shield(1, connectors);
        assertEquals(1, component.getID());
    }

    @RepeatedTest(5)
    void getID_differentID() {
        Component component = new Shield(2, connectors);
        assertEquals(2, component.getID());
    }

    @RepeatedTest(5)
    void rotateClockwise_once() {
        Component component = new Shield(1, connectors);
        component.rotateClockwise();
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_twice() {
        Component component = new Shield(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(2, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_threeTimes() {
        Component component = new Shield(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_fourTimes() {
        Component component = new Shield(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise_multipleFullRotations() {
        Component component = new Shield(1, connectors);
        for (int i = 0; i < 8; i++) {
            component.rotateClockwise();
        }
        assertEquals(0, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenAttachedToShip() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Shield(1, connectors);
        ship.placeComponent(component, 6, 7);
        assertEquals(3, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenNotAttachedToShip_throwsException() {
        Component component = new Shield(1, connectors);
        assertThrows(IllegalStateException.class, component::getExposedConnectors);
    }

    @RepeatedTest(5)
    void getExposedConnectors_withSurroundingComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Shield(1, connectors);
        Component adjacentComponent = new Shield(2, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertEquals(2, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void isConnected_withAdjacentComponent() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Shield(1, connectors);
        Component adjacentComponent = new Shield(2, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isConnected_withMultipleAdjacentComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Shield(1, connectors);
        Component adjacentComponent1 = new Shield(2, connectors);
        Component adjacentComponent2 = new Shield(3, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isFixed_initiallyFalse() {
        Component component = new Shield(1, connectors);
        assertFalse(component.isFixed());
    }

    @RepeatedTest(5)
    void isFixed_afterFixing() {
        Component component = new Shield(1, connectors);
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void isFixed_afterMultipleFixCalls() {
        Component component = new Shield(1, connectors);
        component.fix();
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void fix_setsFixedToTrue() {
        Component component = new Shield(1, connectors);
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void fix_doesNotChangeFixedStateIfAlreadyFixed() {
        Component component = new Shield(1, connectors);
        component.fix();
        component.fix();
        assertTrue(component.isFixed());
    }

    @RepeatedTest(5)
    void isValid_withAllValidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Shield(1, connectors);
        Component adjacentComponent = new Shield(2, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withInvalidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Shield(1, connectors);
        Component adjacentComponent = new Shield(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertFalse(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withTripleConnector() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.TRIPLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Shield(1, connectors);
        Component adjacentComponent = new Shield(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.TRIPLE, ConnectorType.SINGLE});
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withMixedConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Shield(1, connectors);
        Component adjacentComponent1 = new Shield(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        Component adjacentComponent2 = new Shield(3, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertFalse(component.isValid());
    }

/*
    @RepeatedTest(10)
    void getComponentType() {
        s = new Shield(0, connectors);
        ComponentType type = s.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.SHIELD, type);
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, s.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Shield s = new Shield(id, connectors);

        assertEquals(id, s.getID());
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

        Shield s = new Shield(1, connectorArray);

        for(int k = 0; k < 4; k++){
            assertEquals(s.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, s.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            s.rotateClockwise();
        }
        assertEquals(r, s.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = s.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            s.rotateClockwise();
        }

        int b = s.getClockwiseRotation();
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

        Shield s = new Shield(1, connectorArray);
        ship.placeComponent(s, 6, 7);
        if(s.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, s.getExposedConnectors());

        Shield s1 = new Shield(2, connectorArray);
        ship.placeComponent(s1, 6, 8);
        if(s.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, s.getExposedConnectors());
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

            Shield s = new Shield(1, connector);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                s.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(s, 6, 7 + j);

            assertTrue(s.isConnected(6, 7 + j));
        }
    }

    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(s.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Shield[] ss = new Shield[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            ss[j] = new Shield(j, connectors);
            System.out.println(ss[j]);

            ship.placeComponent(ss[j], 6, 7 + j);

            if(j > 0){
                ss[i].fix();
                assertTrue(ss[i].isFixed());
                i++;
            }

            assertFalse(ss[j].isFixed());
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

        Shield shield1 = new Shield(1, connectors1);
        Shield shield2 = new Shield(2, connectors2);

        ship.placeComponent(shield1, 6, 7);
        System.out.println(shield1.isValid());

        if(shield1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(shield1.isValid());
        } else {
            assertFalse(shield1.isValid());
            return;
        }

        ship.placeComponent(shield2, 6, 8);
        System.out.println(shield1.isValid());

        if((shield1.getConnection(3) == ConnectorType.EMPTY && shield2.getConnection(1) != ConnectorType.EMPTY) ||
                (shield1.getConnection(3) == ConnectorType.SINGLE && shield2.getConnection(1) != ConnectorType.SINGLE &&
                        shield2.getConnection(1) != ConnectorType.TRIPLE) ||
                (shield1.getConnection(3) == ConnectorType.DOUBLE && shield2.getConnection(1) != ConnectorType.DOUBLE &&
                        shield2.getConnection(1) != ConnectorType.TRIPLE) ||
                (shield1.getConnection(3) == ConnectorType.TRIPLE && shield2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(shield1.isValid());
        } else {
            assertTrue(shield1.isValid());
        }
    }

    //Reminder: we rotate in sense counterclockwise, so in 0 (N) we protect 0 (N) and 3 (E), in 1 (W) we protect 0 (N) and 1 (W), in 2 (S) we protect 1 (W) and 2 (S), in 3 (E) we protect 2 (S) and 3 (E)
    @RepeatedTest(500)
    void canShield() {
        assertTrue(s.canShield(0));
        assertFalse(s.canShield(1));
        assertFalse(s.canShield(2));
        assertTrue(s.canShield(3));

        Random rand = new Random();
        int r = rand.nextInt(4);
        for(int i = 0; i < r; i++){
            s.rotateClockwise();
        }
        System.out.println("Rotation: " + r%4);

        if(r == 0){
            assertTrue(s.canShield(0));
            assertFalse(s.canShield(1));
            assertFalse(s.canShield(2));
            assertTrue(s.canShield(3));
        } else if(r == 1){
            assertTrue(s.canShield(0));
            assertTrue(s.canShield(1));
            assertFalse(s.canShield(2));
            assertFalse(s.canShield(3));
        } else if(r == 2){
            assertFalse(s.canShield(0));
            assertTrue(s.canShield(1));
            assertTrue(s.canShield(2));
            assertFalse(s.canShield(3));
        } else {
            assertFalse(s.canShield(0));
            assertFalse(s.canShield(1));
            assertTrue(s.canShield(2));
            assertTrue(s.canShield(3));
        }
    }

 */
}