package it.polimi.ingsw.model.cards;

public class Epidemic extends Card {
    /**
     * Constructor
     * @param level level of the card
     * @param ID ID of the card
     */
    public Epidemic(int level, int ID) {
        super(level, ID);
    }

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
