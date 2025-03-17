package Model.SpaceShip;

import java.util.ArrayList;

public abstract class Component {
    protected SpaceShip ship;

    protected int ID;

    protected int row;
    protected int column;

    private boolean fixed;

    private final ConnectorType[] connectors;

    private int clockwiseRotation;

    public Component(int ID, int row, int column, ConnectorType[] connectors) {
        this.ID = ID;
        this.ship = null;
        this.row = row;
        this.column = column;
        this.fixed = false;
        this.connectors = connectors;
        this.clockwiseRotation = 0;
    }

    /**
     * Returns the type of the component
     * @return The type of the component
     */
    public abstract ComponentType getComponentType();

    /**
     * Returns the row of the component
     * @return row of the component
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column of the component
     * @return column of the component
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the connector of the component in the given face
     * @param face The face of the connector (0: north, 1: west, 2: sud, 3: east)
     * @return The connector of the component in the given face
     */
    public ConnectorType getConnection(int face) {
        return connectors[(clockwiseRotation + face) % 4];
    }

    /**
     * Returns the clockwise rotation of the component
     * @return The clockwise rotation of the component (0: 0 degrees, 1: 90 degrees, 2: 180 degrees, 3: 270 degrees)
     */
    public int getClockwiseRotation() {
        return clockwiseRotation;
    }

    /**
     * Returns the ID of the component
     * @return The ID of the component
     */
    public int getID() {
        return ID;
    }

    /**
     * Set the row of the component
     * @param row The row of the component
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Set the column of the component
     * @param column The column of the component
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Rotates the component clockwise (90 degrees)
     */
    public void rotateClockwise() {
        clockwiseRotation = (clockwiseRotation + 1) % 4;
    }

    /**
     * Get the exposed connectors of the component
     * @return The number of exposed connectors of the component
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

    /**
     * Check if the component is connected to the ship
     * @param row The row of the component
     * @param column The column of the component
     * @apiNote Should be called when a component is added to the ship
     * @return true if the component is connected to the ship, false otherwise
     */
    public boolean isConnected(int row, int column) {
        ArrayList<Component> components =  ship.getSurroundingComponents(row, column);
        for (Component c : components) {
            if (c != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the component is fixed and cannot be moved
     * @return true if the component is fixed, false otherwise
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * Fix the component so it cannot be moved
     */
    public void fix() {
        // TODO: we could call here the isConnected method and raise an exception if the component is not connected
        fixed = true;
    }

    /**
     * Check if the component is attached to the right connector
     * @param ship The ship where the component is attached
     * @return true if the component is attached to the right connector, false otherwise
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
