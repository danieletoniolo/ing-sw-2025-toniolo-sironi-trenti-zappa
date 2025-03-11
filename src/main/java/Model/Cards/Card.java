package Model.Cards;

import Model.Player.PlayerData;

public abstract class Card {
    private int level;

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

    public abstract void apply(PlayerData player);
}
