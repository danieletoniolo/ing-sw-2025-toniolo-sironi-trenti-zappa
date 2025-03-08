package Model.SpaceShip;

public class Battery extends Component {
    private int energyNumber;

    public Battery(int row, int column, ConnectorType[] connectors, int energyNumber) {
        super(row, column, connectors);
        this.energyNumber = energyNumber;
    }

    /*
     @brief Get the energy number available of the battery
     @return The energy number available of the battery
     */
    public int getEnergyNumber() {
        return energyNumber;
    }

    /*
     @brief Use an energy from the battery removing one energy from energyNumber
     */
    public void removeEnergy() {
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
