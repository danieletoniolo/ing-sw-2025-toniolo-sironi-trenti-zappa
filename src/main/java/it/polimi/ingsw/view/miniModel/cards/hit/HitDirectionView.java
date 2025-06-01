package it.polimi.ingsw.view.miniModel.cards.hit;

import it.polimi.ingsw.view.miniModel.good.GoodView;

public enum HitDirectionView {
    NORTH(0),
    WEST(1),
    SOUTH(2),
    EAST(3);

    public static String ArrowRight = "→";
    public static String ArrowDown  = "↓";
    public static String ArrowLeft  = "←";
    public static String ArrowUp    = "↑";
    private final int value;

    HitDirectionView(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HitDirectionView fromValue(int value) {
        for (HitDirectionView hit : values()) {
            if (hit.value == value) {
                return hit;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    public String drawTui() {
        return switch (this) {
            case NORTH -> ArrowDown;
            case SOUTH -> ArrowUp;
            case EAST -> ArrowLeft;
            case WEST -> ArrowRight;
        };
    }
}
