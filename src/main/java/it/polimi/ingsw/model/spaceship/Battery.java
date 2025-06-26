package it.polimi.ingsw.model.spaceship;

/**
 * Represents a battery component that stores and provides energy for the spaceship.
 * Extends the Component class to inherit basic component functionality.
 * @author Daniele Toniolo
 */
public class Battery extends Component {
    /** The number of energy units currently stored in the battery */
    private int energyNumber;

    /**
     * Constructs a new Battery with the specified parameters.
     * @param ID The unique identifier for this battery component
     * @param connectors An array of connector types that this battery supports
     * @param energyNumber The initial number of energy units stored in the battery
     */
    public Battery(int ID, ConnectorType[] connectors, int energyNumber) {
        super(ID, connectors);
        this.energyNumber = energyNumber;
    }


    /**
     * Constructs a new Battery with default values.
     * Creates a battery with no energy and default connector configuration.
     */
    public Battery(){
        super();
    }

    /**
     * Get the energy number available of the battery
     * @return The energy number available of the battery
     */
    public int getEnergyNumber() {
        return energyNumber;
    }

    /**
     * Remove an energy from the total available for the component
     */
    public void removeEnergy() throws IllegalStateException {
        if (energyNumber > 0) {
            energyNumber--;
        } else {
            throw new IllegalStateException("Battery has no energy left");
        }
    }

    /**
     * Returns the component type for this battery.
     * This method overrides the abstract method from the Component class
     * to specify that this component is of type BATTERY.
     *
     * @return ComponentType.BATTERY indicating this is a battery component
     */
    @Override
    public ComponentType getComponentType() {
        return ComponentType.BATTERY;
    }
}
