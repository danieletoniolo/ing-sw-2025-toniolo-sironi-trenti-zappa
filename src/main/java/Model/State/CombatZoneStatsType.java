package Model.State;

public enum CombatZoneStatsType {
    CREW(0),
    ENGINES(1),
    CANNONS(2);

    private final int index;

    CombatZoneStatsType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
