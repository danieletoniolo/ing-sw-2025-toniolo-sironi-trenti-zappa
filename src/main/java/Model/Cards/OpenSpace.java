package Model.Cards;

import Model.Player.PlayerData;

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

    @Override
    public void apply(PlayerData player) {

    }
}
