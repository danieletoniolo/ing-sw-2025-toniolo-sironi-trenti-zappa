package Model.SpaceShip;

import java.util.ArrayList;

public abstract class Component {
    private SpaceShip ship;

    protected int row;
    protected int column;

    private boolean fixed;

    private final ConnectorType[] connectors;

    private int clockwiseRotation;

    public Component(int row, int column, ConnectorType[] connectors) {
        ship = null;
        this.row = row;
        this.column = column;
        fixed = false;
        this.connectors = connectors;
        clockwiseRotation = 0;
    }

    /*
     @brief Returns the type of the component
     @return The type of the component
     */
    public abstract ComponentType getComponentType();

    /*
     @brief Returns the row of the component
     @return row
     */
    public int getRow() {
        return row;
    }

    /*
     @brief Returns the column of the component
     @return column
     */
    public int getColumn() {
        return column;
    }

    /*
     @brief Returns the connector of the component in the given orientation
     @param orientation The orientation of the connector (0: north, 1: west, 2: sud, 3: east)
     @return The connector of the component in the given face
     */
    public ConnectorType getConnection(int face) {
        return connectors[(clockwiseRotation + face) % 4];
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
     @brief get the exposed connectors of the component
     @return the number of exposed connectors of the component
     */
    public int getExposedConnectors() {
        int exposedConnector = 0;
        ArrayList<Component> components = ship.getSurroundingComponents(row, column);
        for (int i = 0; i < 4; i++) {
            if (components.get(i) == null && getConnection(i) != ConnectorType.EMPTY) {
                exposedConnector++;
            }
        }
        return exposedConnector;
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
        // TODO: we could call here the isConnected method and raise an exception if the component is not connected
        fixed = true;
    }

    /*
     @brief check if the component is attached to the right connector
     @return true if the component is attached to the right connector, false otherwise
     */
    public boolean isValid(SpaceShip ship) {
        // TODO: check if the ship parameter is null and raise an exception if needed
        ArrayList<Component> components = ship.getSurroundingComponents(row, column);

        for (int face = 0; face < 4; face++) {
            if (components.get(face) != null) {
                if (getConnection(face) != ConnectorType.TRIPLE &&
                        getConnection(face) != components.get(face).getConnection((face + 2) % 4) &&
                        components.get(face).getConnection((face + 2) % 4) != ConnectorType.TRIPLE) {
                    return false;
                }
            }
        }
        this.ship = ship;
        return true;
    }
}
