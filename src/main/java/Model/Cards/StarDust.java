package Model.Cards;

public class StarDust extends Card {
    public StarDust(int level) {
        super(level);
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
