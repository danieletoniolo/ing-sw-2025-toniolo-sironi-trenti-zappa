package Model.Cards;

public class StarDust extends Card {
    public StarDust(int level) {
        super(level);
    }

    @Override
    public CardType getCardType() {
        return CardType.STARDUST;
    }

    @Override
    public void apply(PlayerData player)  {

    }
}
