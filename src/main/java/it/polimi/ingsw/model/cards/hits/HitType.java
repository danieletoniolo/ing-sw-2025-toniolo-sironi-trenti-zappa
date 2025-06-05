package it.polimi.ingsw.model.cards.hits;

import java.io.Serializable;

public enum HitType implements Serializable {
    SMALLMETEOR(0),
    LARGEMETEOR(1),
    LIGHTFIRE(2),
    HEAVYFIRE(3);

    private final int value;

    HitType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HitType fromInt(int value) {
        for (HitType hitType : HitType.values()) {
            if (hitType.value == value) {
                return hitType;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
