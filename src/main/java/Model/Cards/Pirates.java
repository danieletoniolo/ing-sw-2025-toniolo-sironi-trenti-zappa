package Model.Cards;

import Model.Cards.Hits.Hit;

import java.util.List;

public class Pirates extends Enemies {
    private List<Hit> fires;
    private int credit;

    /**
     *
     * @param fire list of hits
     * @param credit number of credit rewarded for the quest
     * @param level level of the card
     * @param cannonStrengthRequired cannon strength of enemies
     * @param flightDays number of flight days lost
     * @throws NullPointerException if fire == null
     */
    public Pirates(List<Hit> fire, int credit, int level, int cannonStrengthRequired, int flightDays) throws NullPointerException {
        super(level, cannonStrengthRequired, flightDays);
        if (fire == null || fire.isEmpty()) {
            throw new NullPointerException("Fire can't be null or empty");
        }
        this.fires = fire;
        this.credit = credit;
    }

    /**
     * Get list of hits
     * @return list of hits
     */
    public List<Hit> getFire() {
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