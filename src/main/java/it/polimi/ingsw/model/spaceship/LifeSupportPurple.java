package it.polimi.ingsw.model.spaceship;

/**
 * Represents a purple life support component for the spaceship.
 * This component extends the base Component class and provides
 * life support functionality with purple classification.
 * @author Daniele Toniolo
 */
public class LifeSupportPurple extends Component {
    /**
     * Constructs a new LifeSupportPurple component with specified ID and connectors.
     *
     * @param ID the unique identifier for this component
     * @param connectors array of connector types available on this component
     */
    public LifeSupportPurple(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    /**
     * Default constructor for LifeSupportPurple component.
     * Creates a component with default values.
     */
    public LifeSupportPurple() {
        super();
    }

    /**
     * Returns the component type for this life support component.
     *
     * @return ComponentType.PURPLE_LIFE_SUPPORT indicating this is a purple life support component
     */
    @Override
    public ComponentType getComponentType() {
        return ComponentType.PURPLE_LIFE_SUPPORT;
    }
}
