package Model.Cards;

public class OpenSpace extends Card {
    public OpenSpace(int level) {
        super(level);
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
