package Model.Cards;

import Model.Cards.Hits.Hit;

import java.util.List;

public class Pirates extends Enemies {
    private List<Hit> fires;
    private int credit;

    public Pirates(List<Hit> fire, int credit, int level, int cannonStrenghtRequired, int flightDays) {
        super(level, cannonStrenghtRequired, flightDays);
        this.fire = fire;
        this.credit = credit;
    }

    public List<Hit> getFire() {
        return fire;
    }

    public int getCredit() {
        return credit;
    }

    @Override
    public CardType getCardType() {
        return CardType.PIRATES;
    }

    @Override
    public void apply(PlayerData player) {

    }
}
