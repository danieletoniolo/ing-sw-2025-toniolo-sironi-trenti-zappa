package Model.SpaceShip;

public class Engine extends Component {
    private int engineStrength;

    public Engine(int ID, ConnectorType[] connectors, int engineStrength) {
        super(ID, connectors);
        this.engineStrength = engineStrength;
    }

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
        Component c = ship.getComponent(row-1, column);
        if (c != null || getClockwiseRotation() != 0) {
            return false;
        }
        return super.isValid();
    }

    @Override
    public ComponentType getComponentType() {
        if (engineStrength == 1) {
            return ComponentType.SINGLE_ENGINE;
        } else {
            return ComponentType.DOUBLE_ENGINE;
        }
    }
}
