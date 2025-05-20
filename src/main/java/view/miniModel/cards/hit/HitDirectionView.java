package view.miniModel.cards.hit;

public enum HitDirectionView {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public static String ArrowRight = "→";
    public static String ArrowDown  = "↓";
    public static String ArrowLeft  = "←";
    public static String ArrowUp    = "↑";

    public String drawTui() {
        return switch (this) {
            case NORTH -> ArrowDown;
            case SOUTH -> ArrowUp;
            case EAST -> ArrowLeft;
            case WEST -> ArrowRight;
        };
    }
}
