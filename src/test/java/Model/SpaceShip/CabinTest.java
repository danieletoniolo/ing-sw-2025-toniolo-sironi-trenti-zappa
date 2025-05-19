package Model.SpaceShip;

import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class CabinTest {
    Cabin c;
    ConnectorType[] connectors;
    Field crewNumberField = Cabin.class.getDeclaredField("crewNumber");
    Field purpleLifeSupportField = Cabin.class.getDeclaredField("purpleLifeSupport");
    Field brownLifeSupportField = Cabin.class.getDeclaredField("brownLifeSupport");
    Field purpleAlienField = Cabin.class.getDeclaredField("purpleAlien");
    Field brownAlienField = Cabin.class.getDeclaredField("brownAlien");
    Field connectorsField = Component.class.getDeclaredField("connectors");
    Field clockwiseRotationField = Component.class.getDeclaredField("clockwiseRotation");
    Field fixedField = Component.class.getDeclaredField("fixed");
    Field shipField = Component.class.getDeclaredField("ship");
    Field IDField = Component.class.getDeclaredField("ID");
    Field rowField = Component.class.getDeclaredField("row");
    Field columnField = Component.class.getDeclaredField("column");

    CabinTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        c = new Cabin(0, connectors);
        assertNotNull(c);
        crewNumberField.setAccessible(true);
        purpleLifeSupportField.setAccessible(true);
        brownLifeSupportField.setAccessible(true);
        purpleAlienField.setAccessible(true);
        brownAlienField.setAccessible(true);
        connectorsField.setAccessible(true);
        clockwiseRotationField.setAccessible(true);
        fixedField.setAccessible(true);
        shipField.setAccessible(true);
        IDField.setAccessible(true);
        rowField.setAccessible(true);
        columnField.setAccessible(true);
    }

    @Test
    void testCabinConstructor() throws IllegalAccessException {
        Cabin cabin = new Cabin();
        assertNotNull(cabin);
        assertEquals(0, IDField.get(cabin));
    }

    @RepeatedTest(5)
    void getCrewNumber_initiallyZero() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        assertEquals(0, crewNumberField.get(cabin));
    }

    @RepeatedTest(5)
    void getCrewNumber_afterAddingCrewMembers() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        cabin.addCrewMember();
        assertEquals(2, crewNumberField.get(cabin));
    }

    @RepeatedTest(5)
    void getCrewNumber_afterAddingPurpleAlien() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addPurpleAlien();
        assertEquals(1, crewNumberField.get(cabin));
    }

    @Test
    void addingPurpleAlienAfterAddingBrownAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        LifeSupportPurple lifeSupportPurple = new LifeSupportPurple(3, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        ship.placeComponent(lifeSupportPurple, 5, 7);
        cabin.isValid();
        cabin.addBrownAlien();
        assertThrows(IllegalStateException.class, cabin::addPurpleAlien);
    }

    @RepeatedTest(5)
    void getCrewNumber_afterAddingBrownAlien() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addBrownAlien();
        assertEquals(1, crewNumberField.get(cabin));
    }

    @Test
    void addingBrownAlienAfterAddingPurpleAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        LifeSupportBrown lifeSupportBrown = new LifeSupportBrown(3, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        ship.placeComponent(lifeSupportBrown, 5, 7);
        cabin.isValid();
        cabin.addPurpleAlien();
        assertThrows(IllegalStateException.class, cabin::addBrownAlien);
    }

    @RepeatedTest(5)
    void getCrewNumber_afterRemovingCrewMembers() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        cabin.addCrewMember();
        cabin.removeCrewMember(1);
        assertEquals(1, crewNumberField.get(cabin));
        cabin.removeCrewMember(1);
        assertEquals(0, crewNumberField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleLifeSupport_initiallyFalse() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        assertFalse((boolean) purpleLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleLifeSupport_afterAddingPurpleLifeSupport() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        assertTrue((boolean) purpleLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleLifeSupport_withNoAdjacentLifeSupport() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        cabin.isValid();
        assertFalse((boolean) purpleLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleLifeSupport_withMultipleAdjacentComponents() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport1 = new LifeSupportPurple(2, connectors);
        LifeSupportPurple lifeSupport2 = new LifeSupportPurple(3, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport1, 6, 8);
        ship.placeComponent(lifeSupport2, 7, 7);
        cabin.isValid();
        assertTrue((boolean) purpleLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownLifeSupport_initiallyFalse() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        assertFalse((boolean) brownLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownLifeSupport_afterAddingBrownLifeSupport() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        assertTrue((boolean) brownLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownLifeSupport_withNoAdjacentLifeSupport() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        cabin.isValid();
        assertFalse((boolean) brownLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownLifeSupport_withMultipleAdjacentComponents() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport1 = new LifeSupportBrown(2, connectors);
        LifeSupportBrown lifeSupport2 = new LifeSupportBrown(3, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport1, 6, 8);
        ship.placeComponent(lifeSupport2, 7, 7);
        cabin.isValid();
        assertTrue((boolean) brownLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleAlien_initiallyFalse() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        assertFalse((boolean) purpleAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleAlien_afterAddingPurpleAlien() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addPurpleAlien();
        assertTrue((boolean) purpleAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleAlien_withNoPurpleLifeSupport() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        assertThrows(IllegalStateException.class, cabin::addPurpleAlien);
        assertFalse((boolean) purpleAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void hasPurpleAlien_withBrownAlienPresent() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addBrownAlien();
        assertThrows(IllegalStateException.class, cabin::addPurpleAlien);
        assertFalse((boolean) purpleAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownAlien_initiallyFalse() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        assertFalse((boolean) brownAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownAlien_afterAddingBrownAlien() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addBrownAlien();
        assertTrue((boolean) brownAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownAlien_withNoBrownLifeSupport() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        assertThrows(IllegalStateException.class, cabin::addBrownAlien);
        assertFalse((boolean) brownAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void hasBrownAlien_withPurpleAlienPresent() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addPurpleAlien();
        assertThrows(IllegalStateException.class, cabin::addBrownAlien);
        assertFalse((boolean) brownAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void addCrewMember_withNoAliens() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        cabin.addCrewMember();
        assertEquals(2, crewNumberField.get(cabin));
    }

    @RepeatedTest(5)
    void addCrewMember_withPurpleAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addPurpleAlien();
        assertThrows(IllegalStateException.class, cabin::addCrewMember);
    }

    @RepeatedTest(5)
    void addCrewMember_withBrownAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addBrownAlien();
        assertThrows(IllegalStateException.class, cabin::addCrewMember);
    }

    @RepeatedTest(5)
    void removeCrewMember_withSufficientCrew() throws IllegalAccessException {
        Cabin cabin = new Cabin(1, connectors);
        cabin.addCrewMember();
        cabin.removeCrewMember(1);
        assertEquals(1, crewNumberField.get(cabin));
        cabin.removeCrewMember(1);
        assertEquals(0, crewNumberField.get(cabin));
    }

    @RepeatedTest(5)
    void removeCrewMember_withInsufficientCrew() {
        Cabin cabin = new Cabin(1, connectors);
        assertThrows(IllegalStateException.class, () -> cabin.removeCrewMember(1));
    }

    @RepeatedTest(5)
    void removeCrewMember_withPurpleAlien() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addPurpleAlien();
        cabin.removeCrewMember(1);
        assertEquals(0, crewNumberField.get(cabin));
        assertFalse((boolean) purpleAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void removeCrewMember_withBrownAlien() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        cabin.isValid();
        cabin.addBrownAlien();
        cabin.removeCrewMember(1);
        assertEquals(0, crewNumberField.get(cabin));
        assertFalse((boolean) brownAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void isValid_withNoAdjacentLifeSupport() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        ship.placeComponent(cabin, 6, 7);
        assertTrue(cabin.isValid());
        assertFalse((boolean) brownAlienField.get(cabin));
        assertFalse((boolean) purpleAlienField.get(cabin));
    }

    @RepeatedTest(5)
    void isValid_withAdjacentPurpleLifeSupport() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport = new LifeSupportPurple(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        assertTrue(cabin.isValid());
        assertTrue((boolean) purpleLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void isValid_withAdjacentBrownLifeSupport() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportBrown lifeSupport = new LifeSupportBrown(2, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport, 6, 8);
        assertTrue(cabin.isValid());
        assertTrue((boolean) brownLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void isValid_withMultipleAdjacentLifeSupports() throws IllegalAccessException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Cabin cabin = new Cabin(1, connectors);
        LifeSupportPurple lifeSupport1 = new LifeSupportPurple(2, connectors);
        LifeSupportBrown lifeSupport2 = new LifeSupportBrown(3, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(lifeSupport1, 6, 8);
        ship.placeComponent(lifeSupport2, 7, 7);
        assertTrue(cabin.isValid());
        assertTrue((boolean) purpleLifeSupportField.get(cabin));
        assertTrue((boolean) brownLifeSupportField.get(cabin));
    }

    @RepeatedTest(5)
    void getComponentType_returnsCabinType() {
        Cabin cabin = new Cabin(1, connectors);
        assertEquals(ComponentType.CABIN, cabin.getComponentType());
    }

    @RepeatedTest(5)
    void getComponentType_withDifferentID() {
        Cabin cabin = new Cabin(2, connectors);
        assertEquals(ComponentType.CABIN, cabin.getComponentType());
    }

    @RepeatedTest(5)
    void getConnection_northFace() {
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Cabin(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(0));
    }

    @RepeatedTest(5)
    void getConnection_westFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component component = new Cabin(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getConnection_southFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Cabin(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(2));
    }

    @RepeatedTest(5)
    void getConnection_eastFace() {
        ConnectorType[] connectors = {ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.SINGLE};
        Component component = new Cabin(1, connectors);
        assertEquals(ConnectorType.SINGLE, component.getConnection(3));
    }

    @RepeatedTest(5)
    void getConnection_afterRotation() {
        ConnectorType[] connectors1 = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.DOUBLE, ConnectorType.EMPTY};
        Component component = new Cabin(1, connectors1);
        component.rotateClockwise();
        assertEquals(ConnectorType.EMPTY, component.getConnection(0));
        assertEquals(ConnectorType.DOUBLE, component.getConnection(1));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_initialValue() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterOneRotation() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        component.rotateClockwise();
        assertEquals(1, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_afterMultipleRotations() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(3, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getClockwiseRotation_fullRotation() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        component.rotateClockwise();
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getID_returnsCorrectID() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        assertEquals(1, IDField.get(component));
    }

    @RepeatedTest(5)
    void getID_differentID() throws IllegalAccessException {
        Component component = new Cabin(2, connectors);
        assertEquals(2, IDField.get(component));
    }

    @RepeatedTest(5)
    void rotateClockwise_multipleFullRotations() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        for (int i = 0; i < 8; i++) {
            component.rotateClockwise();
        }
        assertEquals(0, clockwiseRotationField.get(component));
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenAttachedToShip() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cabin(1, connectors);
        ship.placeComponent(component, 6, 7);
        assertEquals(3, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void getExposedConnectors_whenNotAttachedToShip_throwsException() {
        Component component = new Cabin(1, connectors);
        assertThrows(IllegalStateException.class, component::getExposedConnectors);
    }

    @RepeatedTest(5)
    void getExposedConnectors_withSurroundingComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cabin(1, connectors);
        Component adjacentComponent = new Cabin(2, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertEquals(2, component.getExposedConnectors());
    }

    @RepeatedTest(5)
    void isConnected_withAdjacentComponent() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cabin(1, connectors);
        Component adjacentComponent = new Cabin(2, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isConnected_withMultipleAdjacentComponents() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        Component component = new Cabin(1, connectors);
        Component adjacentComponent1 = new Cabin(2, connectors);
        Component adjacentComponent2 = new Cabin(3, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertTrue(component.isConnected(6, 7));
    }

    @RepeatedTest(5)
    void isFixed_initiallyFalse() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        assertFalse((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isFixed_afterFixing() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isFixed_afterMultipleFixCalls() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        component.fix();
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void fix_setsFixedToTrue() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void fix_doesNotChangeFixedStateIfAlreadyFixed() throws IllegalAccessException {
        Component component = new Cabin(1, connectors);
        component.fix();
        component.fix();
        assertTrue((boolean) fixedField.get(component));
    }

    @RepeatedTest(5)
    void isValid_withAllValidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Cabin(1, connectors);
        Component adjacentComponent = new Cabin(2, connectors);
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withInvalidConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.EMPTY};
        Component component = new Cabin(1, connectors);
        Component adjacentComponent = new Cabin(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertFalse(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withTripleConnector() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.TRIPLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Cabin(1, connectors);
        Component adjacentComponent = new Cabin(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.TRIPLE, ConnectorType.SINGLE});
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent, 6, 8);
        assertTrue(component.isValid());
    }

    @RepeatedTest(5)
    void isValid_withMixedConnections() {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        ConnectorType[] connectors = {ConnectorType.SINGLE, ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Component component = new Cabin(1, connectors);
        Component adjacentComponent1 = new Cabin(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        Component adjacentComponent2 = new Cabin(3, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE});
        ship.placeComponent(component, 6, 7);
        ship.placeComponent(adjacentComponent1, 6, 8);
        ship.placeComponent(adjacentComponent2, 5, 7);
        assertFalse(component.isValid());
    }




/*
    @RepeatedTest(10)
    void getComponentType() {
        c = new Cabin(4,  connectors);
        ComponentType type = c.getComponentType();
        System.out.println(type);

        assertEquals(ComponentType.CABIN, type);
    }

    @RepeatedTest(5)
    void getIDTest(){
        assertEquals(0, c.getID());

        Random rand = new Random();
        int id = rand.nextInt(4,9);
        Cabin cabin = new Cabin(id, connectors );

        assertEquals(id, cabin.getID());
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

        Cabin cabin = new Cabin(4, connectorArray);

        for(int k = 0; k < 4; k++){
            assertEquals(cabin.getConnection(k), check[k]);
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

        Cabin cabin = new Cabin(4, connectorArray);
        ship.placeComponent(cabin, 6, 7);
        if(cabin.getConnection(2) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cabin.getExposedConnectors());

        Cabin cabin1 = new Cabin(5, connectorArray);
        ship.placeComponent(cabin1, 6, 8);
        if(cabin.getConnection(3) != ConnectorType.EMPTY){
            exposed--;
        }
        System.out.println(exposed);
        assertEquals(exposed, cabin.getExposedConnectors());
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

            Cabin cabin = new Cabin(1, connector);

            int r = rand.nextInt(4) + 1;
            for(int p = 0; p < r; p++){
                cabin.rotateClockwise();
            }
            System.out.println("Rotation: " + r%4);

            ship.placeComponent(cabin, 6, 7 + j);

            assertTrue(cabin.isConnected(6, 7 + j));
        }
    }

    //Test for the methods isFixed and fix
    @RepeatedTest(5)
    void isFixedTest(){
        assertFalse(c.isFixed());

        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin[] cs = new Cabin[count];
        int i = 0;
        int j;
        for(j = 0; j < count; j++){
            cs[j] = new Cabin(j, connectors);
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

    @RepeatedTest(1000)
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

        Cabin cabin1 = new Cabin(1,  connectors1);
        Cabin cabin2 = new Cabin(2,  connectors2);

        ship.placeComponent(cabin1, 6, 7);
        System.out.println(cabin1.isValid());

        if(cabin1.getConnection(2) != ConnectorType.EMPTY){
            assertTrue(cabin1.isValid());
        } else {
            assertFalse(cabin1.isValid());
            return;
        }

        ship.placeComponent(cabin2, 6, 8);
        System.out.println(ship.getSurroundingComponents(6, 7));
        System.out.println(ship.getSurroundingComponents(6, 8));
        System.out.println(cabin1.isValid());

        if((cabin1.getConnection(3) == ConnectorType.EMPTY && cabin2.getConnection(1) != ConnectorType.EMPTY) ||
                (cabin1.getConnection(3) == ConnectorType.SINGLE && cabin2.getConnection(1) != ConnectorType.SINGLE &&
                        cabin2.getConnection(1) != ConnectorType.TRIPLE) ||
                (cabin1.getConnection(3) == ConnectorType.DOUBLE && cabin2.getConnection(1) != ConnectorType.DOUBLE &&
                        cabin2.getConnection(1) != ConnectorType.TRIPLE) ||
                (cabin1.getConnection(3) == ConnectorType.TRIPLE && cabin2.getConnection(1) == ConnectorType.EMPTY)){
            assertFalse(cabin1.isValid());
        } else {
            assertTrue(cabin1.isValid());
        }
    }

    @RepeatedTest(5)
    void getCrewNumber() {
        Random rand = new Random();
        int count = rand.nextInt(4) + 1;
        boolean[][] vs = new boolean[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i, connectors);
            ship.placeComponent(cabin, 6, 7 + i);

            cabin.addCrewMember();
            assertEquals(2, cabin.getCrewNumber());

            boolean bool = rand.nextBoolean();
            if(bool){
                cabin.removeCrewMember(1);
                assertEquals(1, cabin.getCrewNumber());
            } else {
                cabin.removeCrewMember(2);
                assertEquals(0, cabin.getCrewNumber());
            }
        }

        LifeSupportPurple lsp = new LifeSupportPurple(10, connectors);
        Cabin cabin1 = new Cabin(1, connectors);
        ship.placeComponent(cabin1, 8, 7);
        ship.placeComponent(lsp, 8, 8);
        cabin1.isValid();
        cabin1.addPurpleAlien();
        assertEquals(1, cabin1.getCrewNumber());
        cabin1.removeCrewMember(1);
        assertEquals(0, cabin1.getCrewNumber());
        assertThrows((IllegalStateException.class), () -> cabin1.removeCrewMember(1));
    }

    @RepeatedTest(5)
    void hasPurpleLifeSupport() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasPurpleLifeSupport());

        LifeSupportPurple c = new LifeSupportPurple(4, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.isValid();
        assertTrue(cabin.hasPurpleLifeSupport());
    }

    @RepeatedTest(5)
    void hasBrownLifeSupport() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasBrownLifeSupport());

        LifeSupportBrown c = new LifeSupportBrown(4, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.isValid();
        assertTrue(cabin.hasBrownLifeSupport());
    }

    @RepeatedTest(5)
    void hasPurpleAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasPurpleAlien());

        LifeSupportPurple c = new LifeSupportPurple(4, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.isValid();
        cabin.addPurpleAlien();

        assertThrows((IllegalStateException.class), cabin::addCrewMember);
        assertTrue(cabin.hasPurpleAlien());
    }

    @RepeatedTest(5)
    void hasBrownAlien() {
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasBrownAlien());

        LifeSupportBrown c = new LifeSupportBrown(4, connectors);
        ship.placeComponent(cabin, 6, 7);
        ship.placeComponent(c, 6, 8);

        cabin.isValid();
        cabin.addBrownAlien();

        assertThrows((IllegalStateException.class), cabin::addCrewMember);
        assertTrue(cabin.hasBrownAlien());
    }

    @RepeatedTest(5)
    void addCrewMember() {
        Random rand = new Random();
        int count = rand.nextInt(2) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i, connectors);
            ship.placeComponent(cabin, 7, 8 + i);

            //Alien
            boolean bool = rand.nextBoolean();
            if(bool){
                boolean bool1 = rand.nextBoolean();
                if(bool1){
                    System.out.println("P");
                    LifeSupportPurple l = new LifeSupportPurple(1, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.isValid();
                    cabin.addPurpleAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), cabin::addCrewMember);
                } else {
                    System.out.println("B");
                    LifeSupportBrown l = new LifeSupportBrown(1, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.isValid();
                    cabin.addBrownAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), cabin::addCrewMember);
                }
            } else {
                System.out.println("N");
                cabin.addCrewMember();
                assertEquals(2, cabin.getCrewNumber());
            }
        }
    }

    @RepeatedTest(5)
    void addPurpleAlien() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasPurpleAlien());
        assertEquals(0, cabin.getCrewNumber());

        boolean bool = rand.nextBoolean();
        if(bool){
            boolean bool1 = rand.nextBoolean();
            if(bool1){
                System.out.println("P");
                LifeSupportPurple l = new LifeSupportPurple(4, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.isValid();
                cabin.addPurpleAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertTrue(cabin.hasPurpleAlien());
            } else {
                System.out.println("B");
                LifeSupportBrown l = new LifeSupportBrown(5, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.isValid();
                cabin.addBrownAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertThrows((IllegalStateException.class), cabin::addPurpleAlien);
            }
        } else {
            //Not alien
            System.out.println("N");
            assertFalse(cabin.hasPurpleAlien());
            assertThrows((IllegalStateException.class), cabin::addPurpleAlien);
        }
    }

    @RepeatedTest(5)
    void addBrownAlien() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasBrownAlien());
        assertEquals(0, cabin.getCrewNumber());

        boolean bool = rand.nextBoolean();
        if(bool){
            boolean bool1 = rand.nextBoolean();
            if(bool1){
                System.out.println("P");
                LifeSupportPurple l = new LifeSupportPurple(4, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.isValid();
                cabin.addPurpleAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertThrows((IllegalStateException.class), cabin::addBrownAlien);
            } else {
                System.out.println("B");
                LifeSupportBrown l = new LifeSupportBrown(5, connectors);
                ship.placeComponent(cabin, 6, 7);
                ship.placeComponent(l, 6, 8);

                cabin.isValid();
                cabin.addBrownAlien();
                assertEquals(1, cabin.getCrewNumber());
                assertTrue(cabin.hasBrownAlien());
            }
        } else {
            //Not alien
            System.out.println("N");
            assertFalse(cabin.hasBrownAlien());
            assertThrows((IllegalStateException.class), cabin::addBrownAlien);
        }
    }

    @RepeatedTest(10)
    void removeCrewMember() {
        Random rand = new Random();
        int count = rand.nextInt(2) + 1;
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        for(int i = 0; i < count; i++){
            Cabin cabin = new Cabin(i , connectors);
            ship.placeComponent(cabin, 7, 8 + i );

            //Alien
            boolean bool = rand.nextBoolean();
            if(bool){
                boolean bool1 = rand.nextBoolean();
                if(bool1){
                    System.out.println("P");
                    LifeSupportPurple l = new LifeSupportPurple(i + 9, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.isValid();
                    cabin.addPurpleAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    cabin.removeCrewMember(1);
                    assertEquals(0, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), () -> cabin.removeCrewMember(1));
                } else {
                    System.out.println("B");
                    LifeSupportBrown l = new LifeSupportBrown(i + 10, connectors);
                    ship.placeComponent(l, 8, 8 + i);

                    cabin.isValid();
                    cabin.addBrownAlien();
                    assertEquals(1, cabin.getCrewNumber());
                    cabin.removeCrewMember(1);
                    assertEquals(0, cabin.getCrewNumber());
                    assertThrows((IllegalStateException.class), () -> cabin.removeCrewMember(1));
                }
            } else {
                System.out.println("N");
                cabin.addCrewMember();
                assertEquals(2, cabin.getCrewNumber());
                cabin.removeCrewMember(1);
                assertEquals(1, cabin.getCrewNumber());
                cabin.removeCrewMember(1);
                assertEquals(0, cabin.getCrewNumber());
                assertThrows((IllegalStateException.class), () -> cabin.removeCrewMember(1));
            }
        }
    }

    @RepeatedTest(5)
    void addPurpleLifeSupport() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasPurpleLifeSupport());
        ship.placeComponent(cabin, 6, 7);

        boolean bool = rand.nextBoolean();
        if(bool){
            System.out.println("P");
            LifeSupportPurple l = new LifeSupportPurple(4, connectors);
            ship.placeComponent(l, 6, 8);
        }

        ArrayList<Component> a = ship.getSurroundingComponents(6, 7);

        for(int i = 0; i < 4; i++){
            System.out.println(i + " " + a.get(i));
            if(a.get(i) instanceof LifeSupportPurple){
                System.out.println("Y");
                cabin.isValid();
                assertTrue(cabin.hasPurpleLifeSupport());
            } else {
                System.out.println("N");
                assertFalse(cabin.hasPurpleLifeSupport());
            }
        }
    }

    @RepeatedTest(5)
    void addBrownLifeSupport() {
        Random rand = new Random();
        SpaceShip ship = new SpaceShip(Level.SECOND, new boolean[12][12]);
        Cabin cabin = new Cabin(3, connectors);
        assertFalse(cabin.hasBrownLifeSupport());
        ship.placeComponent(cabin, 6, 7);

        boolean bool = rand.nextBoolean();
        if(bool){
            System.out.println("B");
            LifeSupportBrown l = new LifeSupportBrown(4, connectors);
            ship.placeComponent(l, 6, 8);
        }

        ArrayList<Component> a = ship.getSurroundingComponents(6, 7);

        for(int i = 0; i < 4; i++){
            System.out.println(i + " " + a.get(i));
            if(a.get(i) instanceof LifeSupportBrown){
                System.out.println("Y");
                cabin.isValid();
                assertTrue(cabin.hasBrownLifeSupport());
            } else {
                System.out.println("N");
                assertFalse(cabin.hasBrownLifeSupport());
            }
        }
    }
 */
}