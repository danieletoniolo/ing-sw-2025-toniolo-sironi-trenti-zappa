package it.polimi.ingsw.model.cards;

/**
 * Represents an Abandoned Ship card in the game.
 * This card type requires crew members and flight days to complete the quest,
 * but rewards the player with credits upon completion.
 * @author Lorenzo Trenti
 */
public class AbandonedShip extends Card {
    /** Number of crew members required and lost for this quest */
    private int crewRequired;
    /** Number of flight days lost for this quest */
    private int flightDays;
    /** Number of credits rewarded upon quest completion */
    private int credit;

    /**
     *
     * @param level level of the card
     * @param ID ID of the card
     * @param crewRequired number of crew members required for the quest and lost
     * @param flightDays number of flight days lost for the quest
     * @param credit number of credit rewarded
     */
    public AbandonedShip(int level, int ID, int crewRequired, int flightDays, int credit) {
        super(level, ID);
        this.crewRequired = crewRequired;
        this.flightDays = flightDays;
        this.credit = credit;
    }

    /**
     * Default constructor for AbandonedShip.
     * Creates an AbandonedShip card with default values.
     */
    public AbandonedShip(){
        super();
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
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.ABANDONEDSHIP;
    }


}
