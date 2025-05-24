package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.hits.Hit;

import java.util.List;

public class Pirates extends Card {
    private int cannonStrengthRequired;
    private int flightDays;

    private List<Hit> fires;
    private int credit;

    /**
     * Constructor
     * @param ID ID of the card
     * @param fires list of hits
     * @param credit number of credit rewarded for the quest
     * @param level level of the card
     * @param cannonStrengthRequired cannon strength of enemies
     * @param flightDays number of flight days lost
     * @throws NullPointerException if fire == null
     */
    public Pirates(int level, int ID, List<Hit> fires, int credit, int cannonStrengthRequired, int flightDays) throws NullPointerException {
        super(level, ID);
        this.cannonStrengthRequired = cannonStrengthRequired;
        this.flightDays = flightDays;
        if (fires == null || fires.isEmpty()) {
            throw new NullPointerException("Fire can't be null or empty");
        }
        this.fires = fires;
        this.credit = credit;
    }

    public Pirates(){
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
     * Get list of hits
     * @return list of hits
     */
    public List<Hit> getFires() {
        return fires;
    }

    /**
     * Get credit rewarded
     * @return credit rewarded
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
        return CardType.PIRATES;
    }

}