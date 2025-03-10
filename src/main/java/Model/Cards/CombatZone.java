package Model.Cards;

import Model.Cards.Hits.Hit;
import Model.Player.PlayerData;

import java.util.List;

public class CombatZone extends Card {
    private int flightDays;
    private int lost;
    private List<Hit> fires;

    public CombatZone(int flightDays, int lost, List<Hit> fires, int level) {
        super(level);
        this.flightDays = flightDays;
        this.lost = lost;
        this.fires = fires;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public int getLost() {
        return lost;
    }

    public List<Hit> getFires() {
        return fires;
    }

    @Override
    public CardType getCardType() {
        return CardType.COMBATZONE;
    }

    @Override
    public void apply(PlayerData player) {

    }
}
