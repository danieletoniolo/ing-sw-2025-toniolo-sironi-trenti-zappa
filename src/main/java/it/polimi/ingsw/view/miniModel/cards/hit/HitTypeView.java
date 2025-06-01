package it.polimi.ingsw.view.miniModel.cards.hit;

import it.polimi.ingsw.view.miniModel.good.GoodView;

public enum HitTypeView {
    SMALLMETEOR(0),
    LARGEMETEOR(1),
    LIGHTFIRE(2),
    HEAVYFIRE(3);
    private final int value;

    HitTypeView(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HitTypeView fromValue(int value) {
        for (HitTypeView hit : values()) {
            if (hit.value == value) {
                return hit;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    public String drawTui() {
        return switch (this) {
            case SMALLMETEOR -> "SM";
            case LARGEMETEOR -> "LM";
            case LIGHTFIRE ->   "LF";
            case HEAVYFIRE ->   "HF";
        };
    }
}
