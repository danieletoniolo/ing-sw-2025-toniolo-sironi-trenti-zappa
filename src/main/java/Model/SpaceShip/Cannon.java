package Model.SpaceShip;

public class Cannon extends Component{
    private final int cannonStrength;

    public Cannon(int ID, ConnectorType[] connectors, int cannonStrength) {
        super(ID, connectors);
        this.cannonStrength = cannonStrength;
    }

    /**
     * Get the strength of the cannon based on the rotation of the cannon
     * @return The strength of the cannon
     */
    public float getCannonStrength() {
        if (getClockwiseRotation() == 0) {
            return cannonStrength;
        } else {
            return ((float) cannonStrength)/2;
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
                case 0 -> ship.getComponent(row + 1, column);
                case 1 -> ship.getComponent(row, column - 1);
                case 2 -> ship.getComponent(row - 1, column);
                case 3 -> ship.getComponent(row, column + 1);
                default -> null;
            };
        }
        return c == null && super.isValid();
    }

    @Override
    public ComponentType getComponentType() {
        if (cannonStrength == 1) {
            return ComponentType.SINGLE_CANNON;
        } else {
            return ComponentType.DOUBLE_CANNON;
        }
    }
}
