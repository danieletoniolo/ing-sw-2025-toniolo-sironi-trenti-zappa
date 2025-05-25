package it.polimi.ingsw.model.spaceship;

public class Battery extends Component {
    private int energyNumber;

    public Battery(int ID, ConnectorType[] connectors, int energyNumber) {
        super(ID, connectors);
        this.energyNumber = energyNumber;
    }


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

    @Override
    public ComponentType getComponentType() {
        return ComponentType.BATTERY;
    }
}
