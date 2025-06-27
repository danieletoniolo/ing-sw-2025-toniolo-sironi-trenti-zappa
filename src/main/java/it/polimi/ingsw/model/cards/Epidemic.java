package it.polimi.ingsw.model.cards;

/**
 * Represents an Epidemic card in the game.
 * This card type extends the base Card class and provides specific functionality for epidemic events.
 * @author Lorenzo Trenti
 */
public class Epidemic extends Card {
    /**
     * Constructor
     * @param level level of the card
     * @param ID ID of the card
     */
    public Epidemic(int level, int ID) {
        super(level, ID);
    }

    /**
     * Default constructor
     */
    public Epidemic() {
        super();
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.EPIDEMIC;
    }

}
