package Model.Cards;

import Model.Player.PlayerData;

public class Slavers extends Enemies {
    private int crewLost;
    private int credit;

    public Slavers(int crewLost, int credit, int level, int cannonStrengthRequired, int flightDays) {
        super(level, cannonStrengthRequired, flightDays);
        this.crewLost = crewLost;
        this.credit = credit;
    }

    public int getReward() {
        return credit;
    }

    public int getCrewLost() {
        return crewLost;
    }

    @Override
    public CardType getCardType() {
        return CardType.SLAVERS;
    }

    @Override
    public void apply(PlayerData player) {

    }
}
