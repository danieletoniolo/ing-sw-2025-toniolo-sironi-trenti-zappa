package Model.Cards;

public class StarDust extends Card {

    /**
     * Constructor
     * @param level level of the card
     * @param ID ID of the card
     */
    public StarDust(int level, int ID) {
        super(level, ID);
    }

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
