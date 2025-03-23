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
        assertNotNull(card, "Card variable not initialized correctly");
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random rand = new Random();
        int level = rand.nextInt(3) + 1;
        Epidemic randomCard = new Epidemic(level, 0);
        assertEquals(level, randomCard.getCardLevel());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.EPIDEMIC, card.getCardType());
    }
}