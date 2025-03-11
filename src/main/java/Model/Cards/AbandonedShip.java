package Model.Cards;

import Model.Player.PlayerData;

public class AbandonedShip extends Card {
    private int crewRequired;
    private int flightDays;
    private int credit;
    private boolean played;

    /**
     *
     * @param level level of the card
     * @param crewRequired number of crew members required and lost
     * @param flightDays flight days lost for the quest
     * @param credit number of credit rewarded
     */
    public AbandonedShip(int level, int crewRequired, int flightDays, int credit) {
        super(level);
        this.crewRequired = crewRequired;
        this.flightDays = flightDays;
        this.credit = credit;
    }

    /**
     * Get the number of crew members required and lost
     * @return number of crew members required and lost
     */
    public int getCrewRequired() {
        return crewRequired;
    }

    /**
     *
     * @return
     */
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
