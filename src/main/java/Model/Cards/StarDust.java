package Model.Cards;

import Model.Player.PlayerData;

import java.util.ArrayList;

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

    @Override
    public void entry(ArrayList<PlayerData> players) {
        //TODO
    }

    @Override
    public void execute(PlayerData player) {
        //TODO

    }

    @Override
    public void exit() {
        //TODO
    }
}
