package Model.SpaceShip;

import java.util.ArrayList;

public abstract class Component {
    private SpaceShip ship;

    protected int row;
    protected int column;

    private int exposedConnector;

    private boolean fixed;

    private final ConnectorType[] connectors;

    private int clockwiseRotation;

    public Component(int row, int column, ConnectorType[] connectors) {
        ship = null;
        this.row = row;
        this.column = column;
        exposedConnector = 0;
        fixed = false;
        this.connectors = connectors;
        clockwiseRotation = 0;
    }

    public int getExposedConnector() {
        return exposedConnector;
    }

    public abstract ComponentType getComponentType();

    public ConnectorType getNorthConnection() {
        return connectors[(clockwiseRotation) % 4];
    }

    public ConnectorType getWestConnection() {
        return connectors[(clockwiseRotation + 1) % 4];
    }

    public ConnectorType getSudConnection() {
        return connectors[(clockwiseRotation + 2) % 4];
    }

    public ConnectorType getEastConnection() {
        return connectors[(clockwiseRotation + 3) % 4];
    }

    /*
     @brief Returns the clockwise rotation of the component
     @return clockwiseRotation
     */
    public int getClockwiseRotation() {
        return clockwiseRotation;
    }

    /*
     @brief Rotates the component clockwise (90 degrees)
     */
    public void rotateClockwise() {
        clockwiseRotation = (clockwiseRotation + 1) % 4;
    }

    /*
     @brief check the exposed connectors of the component and update the exposedConnector variable
     */
    public void checkExposedConnectors() {
        ArrayList<Component> components = ship.getSurroundingComponents(row, column);
        if (components.get(0) == null && getNorthConnection() != ConnectorType.EMPTY) {
            exposedConnector++;
        }
        if (components.get(1) == null && getWestConnection() != ConnectorType.EMPTY) {
            exposedConnector++;
        }
        if (components.get(2) == null && getSudConnection() != ConnectorType.EMPTY) {
            exposedConnector++;
        }
        if (components.get(3) == null && getEastConnection() != ConnectorType.EMPTY) {
            exposedConnector++;
        }
    }

    /*
     @brief check if the component is connected to the ship
     @return true if the component is connected to the ship, false otherwise
     */
    public boolean isConnected() {
        ArrayList<Component> components =  ship.getSurroundingComponents(row, column);
        for (Component c : components) {
            if (c != null) {
                return true;
            }
        }
        return false;
    }

    /*
     @brief check if the component is fixed and cannot be moved
     @return true if the component is fixed, false otherwise
     */
    public boolean isFixed() {
        return fixed;
    }

    /*
     @brief fix the component so it cannot be moved
     */
    public void fix() {
        fixed = true;
    }

    /*
     @brief check if the component is attached to the right connector
     @return true if the component is attached to the right connector, false otherwise
     */
    public boolean isValid(SpaceShip ship) {
        // TODO: check if the ship parameter is null and raise an exception if needed
        ArrayList<Component> components = ship.getSurroundingComponents(row, column);

        // Check the north face of the component with the south face of the above component
        if (components.get(0) == null && getNorthConnection() != ConnectorType.EMPTY) {
            return false;
        }
        if (components.get(0) != null) {
            if (getNorthConnection() != ConnectorType.TRIPLE && getNorthConnection() != components.get(0).getSudConnection()) {
                return false;
            }
        }

        // Check the west face of the component with the east face of the left component
        if (components.get(1) == null && getWestConnection() != ConnectorType.EMPTY) {
            return false;
        }
        if (components.get(1) != null) {
            if (getWestConnection() != ConnectorType.TRIPLE && getWestConnection() != components.get(1).getEastConnection()) {
                return false;
            }
        }

        // Check the south face of the component with the north face of the below component
        if (components.get(2) == null && getSudConnection() != ConnectorType.EMPTY) {
            return false;
        }
        if (components.get(2) != null) {
            if (getSudConnection() != ConnectorType.TRIPLE && getSudConnection() != components.get(2).getNorthConnection()) {
                return false;
            }
        }

        // Check the east face of the component with the west face of the right component
        if (components.get(3) == null && getEastConnection() != ConnectorType.EMPTY) {
            return false;
        }
        if (components.get(3) != null) {
            if (getEastConnection() != ConnectorType.TRIPLE && getEastConnection() != components.get(3).getWestConnection()) {
                return false;
            }
        }
        return true;
    }
}
