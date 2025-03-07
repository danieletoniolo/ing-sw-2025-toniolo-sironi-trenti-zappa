package Model.Cards;

public class AbandonedShip extends Card {
    private int crewRequired;
    private int flightDays;
    private int credit;
    private boolean played;

    public AbandonedShip(int level, int crewRequired, int flightDays, int credit) {
        super(level);
        this.crewRequired = crewRequired;
        this.flightDays = flightDays;
        this.credit = credit;
    }

    public int getCrewRequired() {
        return crewRequired;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public int getCredit() {
        return credit;
    }

    public boolean isPlayed() {
        return played;
    }

    @Override
    public CardType getCardType() {
        return CardType.ABANDONEDSHIP;
    }

    @Override
    public void apply(PlayerData player) {
        played = true;

    }
}
