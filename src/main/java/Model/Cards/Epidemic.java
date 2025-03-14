package Model.Cards;

public class Epidemic extends Card {
    public Epidemic(int level) {
        super(level);
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
