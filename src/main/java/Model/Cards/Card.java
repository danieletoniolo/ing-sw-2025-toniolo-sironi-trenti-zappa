package Model.Cards;

public abstract class Card {
    private final int level;
    /**
     *
     * @param level level of the card
     */
    public Card(int level) {
        this.level = level;
    }

    /**
     * Abstract method : get the card type
     * @return card type
     */
    public abstract CardType getCardType();

    /**
     * Get the card level
     * @return card level
     */
    public int getCardLevel() {
        return level;
    }
}