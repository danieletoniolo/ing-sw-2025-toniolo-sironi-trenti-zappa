package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class StarDustTest {
    StarDust card;
    Field idField = Card.class.getDeclaredField("ID");
    Field levelField = Card.class.getDeclaredField("level");

    StarDustTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        card = new StarDust(2, 0);
        assertNotNull(card);
        idField.setAccessible(true);
        levelField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        StarDust c1 = new StarDust();
        assertNotNull(c1);
        assertEquals(0, idField.get(c1));
        assertEquals(0, levelField.get(c1));
        assertEquals(CardType.STARDUST, c1.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, levelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0, idField.get(card));

        Random rand = new Random();
        int id = rand.nextInt(3) + 1;
        StarDust randomCard = new StarDust(1, id);
        assertEquals(id, idField.get(randomCard));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.STARDUST, card.getCardType());
    }
}