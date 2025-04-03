package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StarDustTest {
    StarDust card;

    @BeforeEach
    void setUp() {
        card = new StarDust(2, 0);
        assertNotNull(card);
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random rand = new Random();
        int level = rand.nextInt(3) + 1;
        StarDust randomCard = new StarDust(level, 0);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(0,card.getID());

        Random rand = new Random();
        int id = rand.nextInt(3) + 1;
        StarDust randomCard = new StarDust(1, id);
        assertEquals(id, randomCard.getID());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.STARDUST, card.getCardType());
    }
}