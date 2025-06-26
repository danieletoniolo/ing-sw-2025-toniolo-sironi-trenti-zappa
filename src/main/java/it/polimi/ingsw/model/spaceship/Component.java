package it.polimi.ingsw.model.spaceship;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Jackson configuration for polymorphic deserialization of Component objects.
 * Uses the "type" property to determine which concrete Component subclass to instantiate.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
/**
 * Defines the mapping between type names and their corresponding Component subclasses.
 * Each subtype represents a different kind of spaceship component that can be serialized/deserialized.
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = Battery.class, name = "Battery"),
        @JsonSubTypes.Type(value = Storage.class, name = "Storage"),
        @JsonSubTypes.Type(value = Cabin.class, name = "Cabin"),
        @JsonSubTypes.Type(value = Connectors.class, name = "Connectors"),
        @JsonSubTypes.Type(value = Engine.class, name = "Engine"),
        @JsonSubTypes.Type(value = Cannon.class, name = "Cannon"),
        @JsonSubTypes.Type(value = LifeSupportBrown.class, name = "LifeSupportBrown"),
        @JsonSubTypes.Type(value = LifeSupportPurple.class, name = "LifeSupportPurple"),
        @JsonSubTypes.Type(value = Shield.class, name = "Shield"),
})
/**
 * Abstract base class representing a component that can be placed on a spaceship.
 * Components have connectors, can be positioned and rotated, and must validate their connections.
 * @author Daniele Toniolo
 */
public abstract class Component implements Serializable {
    /**
     * The spaceship this component is attached to. Null if not yet placed.
     */
    protected SpaceShip ship;

    /**
     * Unique identifier for this component instance.
     */
    @JsonProperty
    protected int ID;

    /**
     * The row position of this component on the spaceship grid.
     */
    protected int row;

    /**
     * The column position of this component on the spaceship grid.
     */
    protected int column;

    /**
     * Array of connector types for each face of the component.
     * Index 0: north, 1: west, 2: south, 3: east (before rotation).
     */
    @JsonProperty
    private ConnectorType[] connectors;

    /**
     * Current clockwise rotation of the component in 90-degree increments.
     * 0: 0°, 1: 90°, 2: 180°, 3: 270°
     */
    private int clockwiseRotation;

    /**
     * Constructs a new Component with the specified ID and connector configuration.
     * The component is initially not attached to any ship and has no rotation.
     *
     * @param ID the unique identifier for this component
     * @param connectors array of connector types for each face of the component
     *                  (index 0: north, 1: west, 2: south, 3: east)
     */
    public Component(int ID, ConnectorType[] connectors) {
        this.ID = ID;
        this.ship = null;
        this.connectors = connectors;
        this.clockwiseRotation = 0;
    }

    /**
     * Default constructor for Component.
     * Used primarily for JSON deserialization.
     */
    public Component(){}

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
     * @throws IllegalStateException if the component is not attached to the ship
     * @apiNote Should only be called after the component is attached to the ship (after the placeComponent method is called)
     */
    public int getExposedConnectors() throws IllegalStateException {
        if (ship == null) {
            throw new IllegalStateException("The component is not attached to the ship");
        }
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
     * Check if the component is attached to the right connector
     * @return true if the component is attached to the right connector, false otherwise
     */
    public boolean isValid() {
        ArrayList<Component> components = ship.getSurroundingComponents(row, column);

        for (int face = 0; face < 4; face++) {
            Component adjacent = components.get(face);
            if (adjacent != null) {
                ConnectorType currentConnection = getConnection(face);
                ConnectorType adjacentConnection = adjacent.getConnection((face + 2) % 4);

                if ((currentConnection == ConnectorType.EMPTY && adjacentConnection != ConnectorType.EMPTY) ||
                        (currentConnection != ConnectorType.EMPTY && adjacentConnection == ConnectorType.EMPTY) ||
                        (currentConnection != ConnectorType.TRIPLE && currentConnection != adjacentConnection && adjacentConnection != ConnectorType.TRIPLE)) {
                    return false;
                }

                if (adjacent.getComponentType() == ComponentType.SINGLE_CANNON || adjacent.getComponentType() == ComponentType.DOUBLE_CANNON) {
                    if ((face == 0 && adjacent.getClockwiseRotation() == 2) ||
                        (face == 1 && adjacent.getClockwiseRotation() == 1) ||
                        (face == 2 && adjacent.getClockwiseRotation() == 0) ||
                        (face == 3 && adjacent.getClockwiseRotation() == 3)) {
                        return false;
                    }
                }

                if (face == 0) {
                    if (adjacent.getComponentType() == ComponentType.SINGLE_ENGINE || adjacent.getComponentType() == ComponentType.DOUBLE_ENGINE) {
                        if (adjacent.getClockwiseRotation() == 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
