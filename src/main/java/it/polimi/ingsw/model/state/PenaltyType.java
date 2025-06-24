package it.polimi.ingsw.model.state;

public enum PenaltyType {
    CREW_PENALTY(0),
    GOODS_PENALTY(1),
    BATTERIES_PENALTY(2),
    HIT_PENALTY(3);

    private final int value;
    PenaltyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
