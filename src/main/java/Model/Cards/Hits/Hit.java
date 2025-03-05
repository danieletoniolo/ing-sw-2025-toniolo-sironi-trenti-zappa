package Model.Cards.Hits;

public class Hit {
    private HitType type;
    private Direction direction;

    public Hit(HitType type, Direction direction) {
        this.type = type;
        this.direction = direction;
    }

    public HitType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }
}
