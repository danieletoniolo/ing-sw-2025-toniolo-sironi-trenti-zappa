package it.polimi.ingsw.model.spaceship;

/**
 * Represents a brown life support component in the spaceship.
 * This component extends the base Component class and provides
 * life support functionality with brown color designation.
 * @author Daniele Toniolo
 */
public class LifeSupportBrown extends Component {
    /**
     * Constructs a LifeSupportBrown component with specified ID and connectors.
     *
     * @param ID the unique identifier for this component
     * @param connectors the array of connector types available on this component
     */
    public LifeSupportBrown(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    /**
     * Default constructor for LifeSupportBrown component.
     * Creates a component with default values.
     */
    public LifeSupportBrown(){
        super();
    }

    /**
     * Returns the component type for this life support component.
     *
     * @return ComponentType.BROWN_LIFE_SUPPORT indicating this is a brown life support component
     */
    @Override
    public ComponentType getComponentType() {
        return ComponentType.BROWN_LIFE_SUPPORT;
    }
}
