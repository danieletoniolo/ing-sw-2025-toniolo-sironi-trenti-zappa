package it.polimi.ingsw.model.spaceship;

import java.util.ArrayList;

/**
 * Shield component that can protect the spaceship from attacks coming from specific directions.
 * A standard shield can protect from top and right directions when not rotated.
 * @author Daniele Toniolo
 */
public class Shield extends Component {
    /**
     * Constructs a Shield with the specified ID and connectors.
     * @param ID The unique identifier for this shield component
     * @param connectors Array of connector types available on this shield
     */
    public Shield(int ID, ConnectorType[] connectors) {
        super(ID, connectors);
    }

    /**
     * Constructs a Shield with default parameters.
     */
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

    /**
     * Returns the component type for this shield.
     * @return ComponentType.SHIELD indicating this is a shield component
     */
    @Override
    public ComponentType getComponentType() {
        return ComponentType.SHIELD;
    }
}
