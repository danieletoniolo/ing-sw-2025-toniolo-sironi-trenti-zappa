package it.polimi.ingsw.model.spaceship;

/**
 * Engine component class that extends Component.
 * Represents a spaceship engine with a specific strength value.
 * @author Daniele Toniolo
 */
public class Engine extends Component {
    /** The strength of the engine (1 for single engine, 2 for double engine) */
    private int engineStrength;

    /**
     * Constructs an Engine with specified parameters.
     * @param ID The unique identifier for this engine component
     * @param connectors Array of connector types for this engine
     * @param engineStrength The strength of the engine (1 or 2)
     */
    public Engine(int ID, ConnectorType[] connectors, int engineStrength) {
        super(ID, connectors);
        this.engineStrength = engineStrength;
    }

    /**
     * Default constructor for Engine.
     * Creates an engine with default values.
     */
    public Engine(){
        super();
    }

    /**
     * Get the engine strength
     * @return The strength of the engine
     */
    public int getEngineStrength() {
        return engineStrength;
    }

    /**
     * @implNote Extends the isValid method from the Component class to check that the engine has no component below it and that it is not rotated
     */
    @Override
    public boolean isValid() {
        Component c = ship.getComponent(row + 1, column);
        if (c != null || getClockwiseRotation() != 0) {
            return false;
        }
        return super.isValid();
    }

    /**
     * Gets the component type based on the engine strength.
     * @return ComponentType.SINGLE_ENGINE if strength is 1, ComponentType.DOUBLE_ENGINE otherwise
     */
    @Override
    public ComponentType getComponentType() {
        if (engineStrength == 1) {
            return ComponentType.SINGLE_ENGINE;
        } else {
            return ComponentType.DOUBLE_ENGINE;
        }
    }
}
