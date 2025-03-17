package Model.Cards;

import Model.Cards.Hits.Hit;
import java.util.List;

public class MeteorSwarm extends Card {
    private List<Hit> meteors;

    /**
     *
     * @param level level of the card
     * @param meteors list of hits
     * @throws NullPointerException meteors == null
     */
    public MeteorSwarm(int level, List<Hit> meteors) throws NullPointerException {
        super(level);
        if (meteors == null || meteors.isEmpty()) {
            throw new NullPointerException("meteors is null");
        }
        this.meteors = meteors;
    }

    /**
     * Get list of meteors
     * @return meteors
     */
    public List<Hit> getMeteors() {
        return meteors;
    }

    /**
     * Get card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.METEORSWARM;
    }
}
