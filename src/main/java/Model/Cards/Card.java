package Model.Cards;

public abstract class Card {
    private final int level;
    private final int ID;
    /**
     *
     * @param level level of the card
     */
    public Card(int level, int ID) {
        this.level = level;
        this.ID = ID;
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

    /**
     * Get the card ID
     * @return card ID
     */
    public int getID() {
        return ID;
    }
}