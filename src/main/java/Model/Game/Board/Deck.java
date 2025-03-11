package Model.Game.Board;

import javax.smartcardio.Card;
import java.util.ArrayList;

public class Deck {
    private final ArrayList<Card> cards;
    private boolean pickable;

    public Deck(ArrayList<Card> cards) {
        this.cards = cards;
        this.pickable = false;
    }

    public boolean isPickable() {
        return pickable;
    }

    public void setPickable(boolean pickable) {
        this.pickable = pickable;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}
