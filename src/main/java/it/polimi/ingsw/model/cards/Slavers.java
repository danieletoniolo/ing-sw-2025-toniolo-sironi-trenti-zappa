package it.polimi.ingsw.model.cards;

public class Slavers extends Card {
    private int cannonStrengthRequired;
    private int flightDays;

    private int crewLost;
    private int credit;

    /**
     * Constructor
     * @param ID ID of the card
     * @param crewLost number of members crew lost
     * @param credit number of credit rewarded
     * @param level level of the card
     * @param cannonStrengthRequired cannon strength of enemies
     * @param flightDays number of flight days lost
     */
    public Slavers(int level, int ID, int crewLost, int credit, int cannonStrengthRequired, int flightDays) {
        super(level, ID);
        this.cannonStrengthRequired = cannonStrengthRequired;
        this.flightDays = flightDays;
        this.crewLost = crewLost;
        this.credit = credit;
    }

    public Slavers(){
        super();
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
