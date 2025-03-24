package Model.SpaceShip;

public class Shield extends Component {
    public Shield(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    /**
     * Check if the shield can shield from the given direction (clockwise from the top)
     * @param direction The clockwise direction to check (0 = top, 1 = right, 2 = bottom, 3 = left)
     */
    public boolean canShield(int direction) {
        /* As a standard shield can shield from top and right */
        return (direction - getClockwiseRotation()) % 4 == 0 || (direction - getClockwiseRotation()) % 4 == 1;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.SHIELD;
    }
}
