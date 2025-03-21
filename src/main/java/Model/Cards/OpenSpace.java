package Model.Cards;

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
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.OPENSPACE;
    }

}
