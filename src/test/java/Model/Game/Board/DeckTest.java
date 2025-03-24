package Model.Game.Board;

import Model.Cards.Card;
import Model.Cards.Epidemic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    Deck deck;
    ArrayList<Card> cards;

    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        deck = new Deck(cards);
    }

    //Test for isPickable and setPickable
    @RepeatedTest(5)
    void isPickable() {
        assertFalse(deck.isPickable());

        Random rand = new Random();
        boolean pickable = rand.nextBoolean();
        System.out.println(pickable);
        deck.setPickable(pickable);
        assertEquals(pickable, deck.isPickable());
    }

    @RepeatedTest(5)
    void getCards() {
        assertEquals(cards, deck.getCards());

        Random rand = new Random();
        int numCards = rand.nextInt(2, 5);
        int i = 0;
        for(i = 0; i < numCards; i++) {
            cards.add(new Epidemic(2, i));
            System.out.println(i);
        }
        Deck newDeck = new Deck(cards);
        assertEquals(cards, newDeck.getCards());
        assertEquals(i, newDeck.getCards().size());
    }
}