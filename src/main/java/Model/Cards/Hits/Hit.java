package Model.Cards.Hits;

public class Hit {
    private HitType type;
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
