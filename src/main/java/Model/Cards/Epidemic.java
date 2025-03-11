package Model.Cards;

import Model.Player.PlayerData;

public class Epidemic extends Card {
    public Epidemic(int level) {
        super(level);
    }

    @Override
    public CardType getCardType() {
        return CardType.EPIDEMIC;
    }

    @Override
    public void apply(PlayerData player) {

    }
}
