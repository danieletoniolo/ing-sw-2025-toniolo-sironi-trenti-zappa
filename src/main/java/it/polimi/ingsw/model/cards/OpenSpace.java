package it.polimi.ingsw.model.cards;

/**
 * Represents an open space card in the game.
 * Open space cards are special types of cards that provide additional gameplay mechanics.
 * @author Lorenzo Trenti
 */
public class OpenSpace extends Card {

    /**
     * Constructor
     * @param level level of the card
     * @param ID ID of the card
     */
    public OpenSpace(int level, int ID) {
        super(level, ID);
    }

    /**
     * Default constructor
     */
    public OpenSpace() {
        super();
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.OPENSPACE;
    }

}
