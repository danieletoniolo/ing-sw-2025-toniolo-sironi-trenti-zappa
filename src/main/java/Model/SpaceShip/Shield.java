package Model.SpaceShip;

public class Shield extends Component {
    public Shield(int ID, int row, int column, ConnectorType[] connectors) {
        super(ID, row, column, connectors);
    }

    /**
     * Check if the shield can shield from the given direction (clockwise from the top)
     * @param direction The clockwise direction to check (0 = top, 1 = right, 2 = bottom, 3 = left)
     */
    public boolean canShield(int direction) {
        /* As a standard shield can shield from top and right */
        if (direction - getClockwiseRotation() == 0 || direction - getClockwiseRotation() == 1) {
            return true;
        }
        return false;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.SHIELD;
    }
}
