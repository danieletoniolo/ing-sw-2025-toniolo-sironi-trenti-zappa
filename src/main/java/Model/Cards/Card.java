package Model.Cards;

public abstract class Card {
    private int level;

    public Card(int level) {
        this.level = level;
    }

    public abstract CardType getCardType();

    public int getCardLevel() {
        return level;
    }

    public abstract void apply(PlayerData player);
}
