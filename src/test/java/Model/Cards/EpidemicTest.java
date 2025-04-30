package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EpidemicTest {
    Epidemic card;

    @BeforeEach
    void setUp() {
        card = new Epidemic(2, 0);
        assertNotNull(card);
    }

    @Test
    void testConstructor() {
        Epidemic c1 = new Epidemic();
        assertNotNull(c1);
        assertEquals(0, c1.getID());
        assertEquals(0, c1.getCardLevel());
        assertEquals(CardType.EPIDEMIC, c1.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random rand = new Random();
        int level = rand.nextInt(3) + 1;
        Epidemic randomCard = new Epidemic(level, 0);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(0,card.getID());

        Random rand = new Random();
        int id = rand.nextInt(3) + 1;
        Epidemic randomCard = new Epidemic(1, id);
        assertEquals(id, randomCard.getID());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.EPIDEMIC, card.getCardType());
    }
}