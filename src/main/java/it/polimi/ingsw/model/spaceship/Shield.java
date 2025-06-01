package it.polimi.ingsw.model.spaceship;

import java.util.ArrayList;

public class Shield extends Component {
    public Shield(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    public Shield() {
        super();
    }

    /**
     * Check if the shield can shield from the given direction (clockwise from the top)
     * @param direction The counterclockwise direction to check (0 = top, 1 = left, 2 = bottom, 3 = right)
     */
    public boolean canShield(int direction) {
        // Convert the direction to the clockwise direction of the shield
        int clockwiseDirection = (4 - direction) % 4;

        /* As a standard shield can shield from top and right */
        int adjustedDirection = (clockwiseDirection - getClockwiseRotation() + 4) % 4;
        return adjustedDirection == 0 || adjustedDirection == 1;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.SHIELD;
    }
}
