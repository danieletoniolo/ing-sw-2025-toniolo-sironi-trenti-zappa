package Model.Cards;

import Model.Player.PlayerData;

import java.util.ArrayList;

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
