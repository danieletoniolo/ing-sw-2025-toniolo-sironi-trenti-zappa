package it.polimi.ingsw.model.cards;

/**
 * StarDust card class that extends the base Card class.
 * Represents a StarDust type card in the game.
 * @author Lorenzo Trenti
 */
public class StarDust extends Card {

    /**
     * Constructor
     * @param level level of the card
     * @param ID ID of the card
     */
    public StarDust(int level, int ID) {
        super(level, ID);
    }

    /**
     * Default constructor
     */
    public StarDust() {
        super();
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.STARDUST;
    }

}
