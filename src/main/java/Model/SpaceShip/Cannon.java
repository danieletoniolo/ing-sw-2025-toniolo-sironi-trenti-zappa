package Model.SpaceShip;

public class Cannon extends Component{
    private final int cannonStrength;

    public Cannon(int row, int column, ConnectorType[] connectors, int cannonStrength) {
        super(row, column, connectors);
        this.cannonStrength = cannonStrength;
    }

    public int getCannonStrength() {
        if (getClockwiseRotation() == 0) {
            return cannonStrength;
        } else {
            return cannonStrength/2;
        }
    }

    @Override
    public boolean isValid(SpaceShip ship) {
        Component c = switch (getClockwiseRotation()) {
            case 0 -> ship.getComponent(row + 1, column);
            case 1 -> ship.getComponent(row, column - 1);
            case 2 -> ship.getComponent(row - 1, column);
            case 3 -> ship.getComponent(row, column + 1);
            default -> null;
        };
        return c == null && super.isValid(ship);
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
