package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.hits.Hit;
import java.util.List;

/**
 * MeteorSwarm card that contains a list of meteor hits.
 * Extends the base Card class and represents a special type of card
 * that can contain multiple hit effects.
 * @author Lorenzo Trenti
 */
public class MeteorSwarm extends Card {
    /** List of meteor hits associated with this card */
    private List<Hit> meteors;

    /**
     * Constructor
     * @param level level of the card
     * @param ID ID of the card
     * @param meteors list of hits
     * @throws NullPointerException meteors == null
     */
    public MeteorSwarm(int level, int ID, List<Hit> meteors) throws NullPointerException {
        super(level, ID);
        if (meteors == null || meteors.isEmpty()) {
            throw new NullPointerException("meteors is null");
        }
        this.meteors = meteors;
    }

    /**
     * Default constructor for MeteorSwarm.
     * Creates an empty MeteorSwarm card with default values.
     */
    public MeteorSwarm() {
        super();
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
