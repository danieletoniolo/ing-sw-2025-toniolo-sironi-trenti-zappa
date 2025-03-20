package Model.State;

public enum CombatZoneStatsType {
    CREW(0),
    ENGINES(1),
    CANNONS(2);

    private final int index;

    CombatZoneStatsType(int index) {
        this.index = index;
    }

    public int getIndex(int level) {
        int out = this.index;
        if (level == 2) {
            if (index == 0) {
                out = 2;
            } else if (index == 2) {
                out = 0;
            }
        }
        return out;
    }
}
