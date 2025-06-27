package it.polimi.ingsw.model.cards.hits;

import java.io.Serializable;

/**
 * Represents a hit with a specific type and direction.
 * This class implements Serializable to allow for object serialization.
 * @author Lorenzo Trenti
 */
public class Hit implements Serializable {
    /** The type of the hit */
    private HitType type;
    /** The direction of the hit */
    private Direction direction;

    /**
     *
     * @param type type of the hit
     * @param direction direction of the hit: es: east comes from east
     */
    public Hit(HitType type, Direction direction) {
        this.type = type;
        this.direction = direction;
    }

    /**
     * Default constructor for Hit class.
     * Creates a Hit instance with null type and direction.
     */
    public Hit(){}

    /**
     * Get type of the hit
     * @return type of the hit
     */
    public HitType getType() {
        return type;
    }

    /**
     * Get direction of the hit
     * @return direction of the hit
     */
    public Direction getDirection() {
        return direction;
    }
}
