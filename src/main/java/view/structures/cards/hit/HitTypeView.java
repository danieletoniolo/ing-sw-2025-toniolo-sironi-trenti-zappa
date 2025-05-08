package view.structures.cards.hit;

public enum HitTypeView {
    SMALLMETEOR,
    LARGEMETEOR,
    LIGHTFIRE,
    HEAVYFIRE;

    public String drawTui() {
        return switch (this) {
            case SMALLMETEOR -> "SMeteor";
            case LARGEMETEOR -> "LMeteor";
            case LIGHTFIRE ->   "LFire  ";
            case HEAVYFIRE ->   "HFife  ";
        };
    }
}
