package Model.Cards;

public class Slavers extends Card {
    private final int cannonStrengthRequired;
    private final int flightDays;

    private final int crewLost;
    private final int credit;

    /**
     *
     * @param crewLost number of members crew lost
     * @param credit number of credit rewarded
     * @param level level of the card
     * @param cannonStrengthRequired cannon strength of enemies
     * @param flightDays number of flight days lost
     */
    public Slavers(int crewLost, int credit, int level, int cannonStrengthRequired, int flightDays) {
        super(level);
        this.cannonStrengthRequired = cannonStrengthRequired;
        this.flightDays = flightDays;
        this.crewLost = crewLost;
        this.credit = credit;
    }

    /**
     * Get the cannon power required to beat the card
     * @return cannon power required
     */
    public int getCannonStrengthRequired() {
        return cannonStrengthRequired;
    }

    /**
     * Get the number of flight days lost for the quest
     * @return number of flight days lost for the quest
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Get number of credit rewarded
     * @return number of credit rewarded
     */
    public int getCredit() {
        return credit;
    }

    /**
     * Get the number of crew members lost
     * @return number of crew members lost
     */
    public int getCrewLost() {
        return crewLost;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.SLAVERS;
    }

}
