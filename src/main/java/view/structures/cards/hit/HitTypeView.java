package view.structures.cards.hit;

public enum HitTypeView {
    SMALLMETEOR,
    LARGEMETEOR,
    LIGHTFIRE,
    HEAVYFIRE;

    public String drawTui() {
        return switch (this) {
            case SMALLMETEOR -> "SM";
            case LARGEMETEOR -> "LM";
            case LIGHTFIRE ->   "LF";
            case HEAVYFIRE ->   "HF";
        };
    }
}
