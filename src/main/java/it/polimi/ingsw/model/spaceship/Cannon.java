package it.polimi.ingsw.model.spaceship;

/**
 * Represents a cannon component that can be mounted on a spaceship.
 * The cannon's effectiveness depends on its rotation and strength.
 * @author Daniele Toniolo
 */
public class Cannon extends Component{
    /** The base strength value of this cannon */
    private int cannonStrength;

    /**
     * Constructs a new Cannon with specified parameters.
     *
     * @param ID the unique identifier for this cannon component
     * @param connectors the array of connector types for this cannon
     * @param cannonStrength the base strength value of the cannon
     */
    public Cannon(int ID, ConnectorType[] connectors, int cannonStrength) {
        super(ID, connectors);
        this.cannonStrength = cannonStrength;
    }

    /**
     * Constructs a new Cannon with default parameters.
     * This constructor creates a cannon with default values.
     */
    public Cannon(){
        super();
    }

    /**
     * Get the strength of the cannon based on the rotation of the cannon
     * @return The strength of the cannon
     */
    public float getCannonStrength() {
        if (getClockwiseRotation() == 0) {
            return cannonStrength;
        } else {
            return (float) cannonStrength/2;
        }
    }

    /**
     * @implNote Extend the isValid method to check if the cannon has no component in front of it
     */
    @Override
    public boolean isValid() {
        Component c = null;
        if (ship != null) {
            c = switch (getClockwiseRotation()) {
                case 0 -> ship.getComponent(row - 1, column);
                case 1 -> ship.getComponent(row, column + 1);
                case 2 -> ship.getComponent(row + 1, column);
                case 3 -> ship.getComponent(row, column - 1);
                default -> null;
            };
        }
        return c == null && super.isValid();
    }

    /**
     * Returns the component type of this cannon based on its strength.
     *
     * @return ComponentType.SINGLE_CANNON if cannon strength is 1,
     *         ComponentType.DOUBLE_CANNON otherwise
     */
    @Override
    public ComponentType getComponentType() {
        if (cannonStrength == 1) {
            return ComponentType.SINGLE_CANNON;
        } else {
            return ComponentType.DOUBLE_CANNON;
        }
    }
}
