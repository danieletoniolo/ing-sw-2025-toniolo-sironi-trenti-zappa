package Model.Game.Board;

import Model.Cards.Card;

import java.util.ArrayList;

public class Deck {
    private final ArrayList<Card> cards;
    private boolean pickable;

    /**
     * Create a new deck
     * @param cards the cards in the deck
     */
    public Deck(ArrayList<Card> cards) {
        this.cards = cards;
        this.pickable = false;
    }

    /**
     * Get if the deck is pickable
     * @return if the deck is pickable
     */
    public boolean isPickable() {
        return pickable;
    }

    /**
     * Set if the deck is pickable
     * @param pickable if the deck is pickable
     */
    public void setPickable(boolean pickable) {
        this.pickable = pickable;
    }

    /**
     * Get the cards in the deck
     * @return the cards in the deck
     */
    public ArrayList<Card> getCards() {
        return cards;
    }
}
