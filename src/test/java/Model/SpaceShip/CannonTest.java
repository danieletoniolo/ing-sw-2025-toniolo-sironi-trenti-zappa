package Model.SpaceShip;

import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.spaceship.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class CannonTest {
    Cannon component;
    ConnectorType[] connectors;
    Field cannonStrengthField = Cannon.class.getDeclaredField("cannonStrength");
    Field connectorsField = Component.class.getDeclaredField("connectors");
    Field clockwiseRotationField = Component.class.getDeclaredField("clockwiseRotation");
    Field fixedField = Component.class.getDeclaredField("fixed");
    Field shipField = Component.class.getDeclaredField("ship");
    Field IDField = Component.class.getDeclaredField("ID");
    Field rowField = Component.class.getDeclaredField("row");
    Field columnField = Component.class.getDeclaredField("column");

    CannonTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE , ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        component = new Cannon(0, connectors, 1);
        assertNotNull(component);
        cannonStrengthField.setAccessible(true);
        connectorsField.setAccessible(true);
        clockwiseRotationField.setAccessible(true);
        fixedField.setAccessible(true);
        shipField.setAccessible(true);
        IDField.setAccessible(true);
        rowField.setAccessible(true);
        columnField.setAccessible(true);
    }

    @Test
    void testCannonConstructor() throws IllegalAccessException {
        Cannon a = new Cannon();
        assertNotNull(a);
        assertEquals(0, IDField.get(a));
        assertFalse((boolean) fixedField.get(a));
        assertEquals(0, cannonStrengthField.get(a));
    }

    @RepeatedTest(5)
    void getCannonStrength_withNoRotation() throws IllegalAccessException {
        Cannon cannon = new Cannon(1, connectors, 2);
        assertEquals(2, cannonStrengthField.get(cannon));
        Cannon cannon1 = new Cannon(1, connectors, 1);
        assertEquals(1, cannonStrengthField.get(cannon1));
    }

    @RepeatedTest(5)
    void getCannonStrength_withOneRotation() throws IllegalAccessException {
        Cannon cannon = new Cannon(1, connectors, 2);
        cannon.rotateClockwise();
        assertEquals(1.0, cannon.getCannonStrength());
        Cannon cannon1 = new Cannon(1, connectors, 1);
        cannon1.rotateClockwise();
        assertEquals(0.5, cannon1.getCannonStrength());
    }

    @RepeatedTest(5)
    void getCannonStrength_withTwoRotations() throws IllegalAccessException {
        Cannon cannon = new Cannon(1, connectors, 2);
        cannon.rotateClockwise();
        cannon.rotateClockwise();
        assertEquals(1.0, cannon.getCannonStrength());
        Cannon cannon1 = new Cannon(1, connectors, 1);
        cannon1.rotateClockwise();
        cannon1.rotateClockwise();
        assertEquals(0.5, cannon1.getCannonStrength());
    }

    @RepeatedTest(5)
    void getCannonStrength_withThreeRotations() throws IllegalAccessException {
        Cannon cannon = new Cannon(1, connectors, 2);
        cannon.rotateClockwise();
        cannon.rotateClockwise();
        cannon.rotateClockwise();
        assertEquals(1.0, cannon.getCannonStrength());
        Cannon cannon1 = new Cannon(1, connectors, 1);
        cannon1.rotateClockwise();
        cannon1.rotateClockwise();
        cannon1.rotateClockwise();
        assertEquals(0.5, cannon1.getCannonStrength());
    }

    @RepeatedTest(5)
    void getCannonStrength_withFullRotation() throws IllegalAccessException {
        Cannon cannon = new Cannon(1, connectors, 2);
        for (int i = 0; i < 4; i++) {
            cannon.rotateClockwise();
        }
        assertEquals(2, cannonStrengthField.get(cannon));
        Cannon cannon1 = new Cannon(1, connectors, 1);
        for (int i = 0; i < 4; i++) {
            cannon1.rotateClockwise();
        }
        assertEquals(1, cannonStrengthField.get(cannon1));
    }

    @RepeatedTest(5)
    void isValid_withNoComponentInFront() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        assertTrue(cannon.isValid());
    }

    @RepeatedTest(5)
    void isValid_withComponentInFront() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cannon cannon = new Cannon(1, connectors, 2);
        Storage storage = new Storage(2, connectors, true, 1);
        ship.placeComponent(cannon, 6, 7);
        ship.placeComponent(storage, 5, 7);
        assertFalse(cannon.isValid());
    }

    @RepeatedTest(5)
    void isValid_withComponentOnSide() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cannon cannon = new Cannon(1, connectors, 2);
        Storage storage = new Storage(2, connectors, true, 1);
        ship.placeComponent(cannon, 5, 6);
        ship.placeComponent(storage, 5, 7);
        assertTrue(cannon.isValid());
    }

    @RepeatedTest(5)
    void isValid_withComponentInFrontAfterRotation() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        cannon.rotateClockwise();
        assertFalse(cannon.isValid());
    }

    @RepeatedTest(5)
    void isValid_after2Rotation() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        cannon.rotateClockwise();
        cannon.rotateClockwise();
        assertTrue(cannon.isValid());
    }

    @RepeatedTest(5)
    void isValid_after3Rotation() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cannon cannon = new Cannon(1, connectors, 2);
        ship.placeComponent(cannon, 6, 7);
        cannon.rotateClockwise();
        cannon.rotateClockwise();
        cannon.rotateClockwise();
        assertTrue(cannon.isValid());
    }

    @Test
    void isValid_withoutShip() {
        Cannon cannon = new Cannon(1, connectors, 2);
        assertThrows(NullPointerException.class, cannon::isValid);
    }

    @RepeatedTest(5)
    void getComponentType_withNoConnectors() {
        Cannon cannon = new Cannon(3, connectors, 1);
        assertEquals(ComponentType.SINGLE_CANNON, cannon.getComponentType());
        Cannon cannon1 = new Cannon(4, connectors, 2);
        assertEquals(ComponentType.DOUBLE_CANNON, cannon1.getComponentType());
    }

    @RepeatedTest(5)
    void getConnection_northFace() {
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Cannon(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(0));
    }

    @RepeatedTest(5)
    void getConnection_westFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Cannon(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getConnection_southFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Cannon(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(2));
    }

    @RepeatedTest(5)
    void getConnection_eastFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE};
        Component component = new Cannon(1, connectors, 1);
        assertEquals(ConnectorType.SINGLE, component.getConnection(3));
    }

    @RepeatedTest(5)
    void getConnection_afterRotation() {
        ConnectorType[] connectors1 = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.DOUBLE, ConnectorType.EMPTY};
        Component component = new Cannon(1, connectors1, 1);
        component.rotateClockwise();
        assertEquals(ConnectorType.EMPTY, component.getConnection(0));
        assertEquals(ConnectorType.DOUBLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_initialValue() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterOneRotation() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        component.rotateClockwise();
        assertEquals(1, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterMultipleRotations() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_fullRotation() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getID_returnsCorrectID() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        assertEquals(1, IDField.get(component));
    }

    @RepeatedTest(5)
    void getID_differentID() throws IllegalAccessException {
        Component component = new Cannon(2, connectors, 1);
        assertEquals(2, IDField.get(component));
    }

    @RepeatedTest(5)
    void rotateClockwise_multipleFullRotations() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        for (int i = 0; i < 8; i++) {
            component.rotateClockwise();
        }
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenAttachedToShip() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cannon(1, connectors, 1);
        ship.placeComponent(component, 6, 7);
        assertEquals(3, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenNotAttachedToShip_throwsException() {
        Component component = new Cannon(1, connectors, 1);
        assertThrows(IllegalStateException.class, component::getExposedConnectors);
    }

    @RepeatedTest(5)
    void getExposedConnectors_withSurroundingComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cannon(1, connectors, 1);
        Component adjacentComponent = new Cannon(2, connectors, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertEquals(2, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void isConnected_withAdjacentComponent() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cannon(1, connectors, 1);
        Component adjacentComponent = new Cannon(2, connectors, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isConnected_withMultipleAdjacentComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cannon(1, connectors, 1);
        Component adjacentComponent1 = new Cannon(2, connectors, 1);
        Component adjacentComponent2 = new Cannon(3, connectors, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 7, 7);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isFixed_initiallyFalse() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        assertFalse((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isFixed_afterFixing() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isFixed_afterMultipleFixCalls() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        component.fix();
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void fix_setsFixedToTrue() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void fix_doesNotChangeFixedStateIfAlreadyFixed() throws IllegalAccessException {
        Component component = new Cannon(1, connectors, 1);
        component.fix();
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isValid_withAllValidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Cannon(1, connectors, 1);
        Component adjacentComponent = new Cannon(2, connectors, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withInvalidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Cannon(1, connectors, 1);
        Component adjacentComponent = new Cannon(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertFalse(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withTripleConnector() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.TRIPLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Cannon(1, connectors, 1);
        Component adjacentComponent = new Cannon(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.TRIPLE, ConnectorType.SINGLE}, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withMixedConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Cannon(1, connectors, 1);
        Component adjacentComponent1 = new Cannon(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY}, 1);
        Component adjacentComponent2 = new Cannon(3, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, 1);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 7, 7);
        assertTrue(component.isValid());
    }

/*
    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, component.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Cannon cannon = new Cannon(id, connectors, 1);

        assertEquals(id, cannon.getID());
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

        Cannon cannon = new Cannon(1, connectorArray, 1);

        for(int k = 0; k < 4; k++){
            assertEquals(cannon.getConnection(k), check[k]);
        }
    }

    @RepeatedTest(5)
    void isValidTest(){
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Storage storage = new Storage(1, connectors, true, 1);
        ship.placeComponent(storage, 8, 7);
        Random rand = new Random();
        int i = rand.nextInt(4) + 1;
        for(int j = 0; j < i; j++) {
            Cannon cannon = new Cannon(j, connectors, 1);
            ship.placeComponent(cannon, 8, 8 + j);

            boolean add = rand.nextBoolean();
            System.out.println(add);
            if(add){
                Cannon cannon1 = new Cannon(5 + j, connectors, 1);
                ship.placeComponent(cannon1, 7, 8 + j);
            }

            assertNotEquals(add, cannon.isValid());
        }
    }

    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(component.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cannon[] cs = new Cannon[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            cs[j] = new Cannon(j, connectors, 2);
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

        Cannon cannon = new Cannon(1, connectorArray, 1);
        ship.placeComponent(cannon, 6, 7);
        if(cannon.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cannon.getExposedConnectors());

        Cannon cannon1 = new Cannon(2, connectorArray, 1);
        ship.placeComponent(cannon1, 6, 8);
        if(cannon1.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cannon.getExposedConnectors());
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

            Cannon cannon = new Cannon(1, connector, 1);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                cannon.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cannon, 6, 7 + j);

            assertTrue(cannon.isConnected(6, 7 + j));
        }
    }

    @RepeatedTest(5)
    void getClockwiseRotation() {
        assertEquals(0, component.getClockwiseRotation());

        Random rand = new Random();
        int r = rand.nextInt(4);

        for(int k = 0; k < r; k++){
            component.rotateClockwise();
        }
        assertEquals(r, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateClockwise() {
        Random rand = new Random();
        int r = rand.nextInt(4);

        int a = component.getClockwiseRotation() + r;

        for(int k = 0; k < r; k++){
            component.rotateClockwise();
        }

        int b = component.getClockwiseRotation();
        assertEquals(a, b);
    }

    @RepeatedTest(5)
    void getCannonStrength() {
        assertEquals(1, component.getCannonStrength());

        Random rand = new Random();
        int r = rand.nextInt(4);
        int power = rand.nextInt(1,3);
        Cannon cannon = new Cannon(0, connectors, power);
        for(int k = 0; k < r; k++){
            cannon.rotateClockwise();
        }

        if(cannon.getClockwiseRotation() != 0){
            float ris = (float) power / 2;
            System.out.println(ris);
            assertEquals(ris, cannon.getCannonStrength());
        } else {
            assertEquals(power, cannon.getCannonStrength());
        }

    }

    @RepeatedTest(10)
    void getComponentType() {
        Random rand = new Random();
        int i = rand.nextInt(2) + 1;

        component = new Cannon(0, connectors, i);
        ComponentType type = component.getComponentType();
        System.out.println(type);

        if(type == ComponentType.SINGLE_CANNON) {
            assertEquals(1, i);
        } else {
            assertEquals(2, i);
        }
    }
 */
}