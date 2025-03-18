package Model.Cards;

import Model.Good.Good;

import java.util.List;

public class AbandonedStation extends Card {
    private int crewRequired;
    private int flightDays;
    private List<Good> goods;
    private boolean played;

    /**
     *
     * @param level level of the card
     * @param crewRequired number of crew member required for the quest and not lost
     * @param flightDays number of flight days lost for the quest
     * @param goods list of goods rewarded
     * @throws NullPointerException if goods == null
     */
    public AbandonedStation(int level, int crewRequired, int flightDays, List<Good> goods) throws NullPointerException{
        super(level);
        this.crewRequired = crewRequired;
        this.flightDays = flightDays;

        if (goods == null || goods.isEmpty()) {
            throw new NullPointerException("Good list is null or empty");
        }
        this.goods = goods;
    }

    /**
     * Get the number of crew members required for the quest
     * @return number of crew members required for the quest
     */
    public int getCrewRequired() {
        return crewRequired;
    }

    /**
     * Get the number of flight days lost for the quest
     * @return number of flight days lost for the quest
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Get the list of goods rewarded
     * @return list of goods rewarded
     */
    public List<Good> getGoods() {
        return goods;
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
        return CardType.ABANDONEDSTATION;
    }

}
