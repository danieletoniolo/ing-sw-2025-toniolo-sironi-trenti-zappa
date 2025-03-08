package Model.SpaceShip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CannonTest {
    Cannon component;
    ConnectorType[] connectors;

    @BeforeEach
    void setUp() {
        connectors = ConnectorType.values();
        component = new Cannon(1, 2, connectors, 1);
        assertNotNull(component, "Component not initialized correctly");
    }

    //Da fare forse un esempio per:
    //getNorthConnector, ...
    //checkExposedConnectors
    //isConnected
    //...

    @Test
    void getClockwiseRotation() {
        assertEquals(0, component.getClockwiseRotation(), "Clockwise rotation should be zero");
    }

    @Test
    void rotateClockwise() {
        int a = component.getClockwiseRotation() + 1;
        component.rotateClockwise();
        int b = component.getClockwiseRotation();
        assertEquals(a, b, "Error in clockwise rotation");
    }

    @Test
    void getCannonStrength() {
        assertEquals(1, component.getCannonStrength(), "Error in cannon strength");
    }

    @Test
    void isValid() {
    }

    @Test
    void getComponentType() {
        if(component.getCannonStrength() == 1) {
            assertEquals(ComponentType.SINGLE_CANNON, component.getComponentType(), "Error in component type");
        } else {
            assertEquals(ComponentType.DOUBLE_CANNON, component.getComponentType(), " Error in component type");
        }
    }
}