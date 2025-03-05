package Model.Cards;

import Model.Good.Good;

import java.util.List;

public class AbandonedStation extends Card {
    private int crewRequired;
    private int flightDays;
    private List<Good> goods;
    private boolean played;

    public AbandonedStation(int level, int crewRequired, int flightDays, List<Good> goods) {
        super(level);
        this.crewRequired = crewRequired;
        this.flightDays = flightDays;
        this.goods = goods;
    }

    public int getCrewRequired() {
        return crewRequired;
    }

    public int getFlightDays() {
        return flightDays;
    }

    private List<Good> getGoods() {
        return goods;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed() {
        played = true;
    }

    @Override
    public CardType getCardType() {
        return CardType.ABANDONEDSTATION;
    }

    @Override
    public void apply(PlayerData player) {

    }
}
