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
        card = new StarDust(2);
        assertNotNull(card);
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.STARDUST, card.getCardType());
    }

    @Test
    void apply() {

    }

    @RepeatedTest(5)
    void testRandomizedLevelInitialization() {
        Random random = new Random();
        int level = random.nextInt(card.getCardLevel()) + 1;
        card = new StarDust(level);
        assertEquals(level, card.getCardLevel());
    }
}