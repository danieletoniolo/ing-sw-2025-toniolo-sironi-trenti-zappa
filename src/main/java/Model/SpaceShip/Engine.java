package Model.SpaceShip;

import java.util.ArrayList;

public class Engine extends Component {
    private final int engineStrength;

    public Engine(int row, int column, ConnectorType[] connectors, int engineStrength) {
        super(row, column, connectors);
        this.engineStrength = engineStrength;
    }

    /*
     @brief Get the engine strength
     @return engineStrength
     */
    public int getEngineStrength() {
        return engineStrength;
    }

    /*
     @brief Check if the engine is valid
     @param ship must be a non-null SpaceShip
     @return true if the engine is valid, false otherwise
     */
    @Override
    public boolean isValid(SpaceShip ship) {
        Component c = ship.getComponent(row-1, column);
        if (c != null || getClockwiseRotation() != 0) {
            return false;
        }
        return super.isValid(ship);
    }

    /*
     @brief Get the component type
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
