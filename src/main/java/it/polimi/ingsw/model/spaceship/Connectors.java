package it.polimi.ingsw.model.spaceship;

/**
 * Represents a connectors component in the spaceship.
 * This class extends Component and provides functionality for managing connector-type components.
 * @author Daniele Toniolo
 */
public class Connectors extends Component {
    /**
     * Constructs a Connectors object with the specified ID and connector types.
     *
     * @param ID the unique identifier for this component
     * @param connectors an array of ConnectorType objects defining the available connectors
     */
    public Connectors(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    /**
     * Default constructor for Connectors.
     * Creates a Connectors object with default values.
     */
    public Connectors(){
        super();
    }

    /**
     * Returns the component type for this object.
     *
     * @return ComponentType.CONNECTORS indicating this is a connectors component
     */
    @Override
    public ComponentType getComponentType() {
        return ComponentType.CONNECTORS;
    }
}
