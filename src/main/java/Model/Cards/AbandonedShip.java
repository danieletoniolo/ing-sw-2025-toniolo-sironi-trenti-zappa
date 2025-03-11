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
     * @param crewRequired number of crew members required for the quest and lost
     * @param flightDays number of flight days lost for the quest
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
     * Get the number of flight days lost
     * @return number of flight days lost
     */
    public int getFlightDays() {
        return flightDays;
    }


    /**
     * Get the number of credit rewarded
     * @return number of credit rewarded
     */
    public int getCredit() {
        return credit;
    }

    /**
     * Verify if the card is played by a player
     * @return boolean value that verify if the card is played
     */
    public boolean isPlayed() {
        return played;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.ABANDONEDSHIP;
    }

    @Override
    public void apply(PlayerData player) {
        played = true;

    }
}
