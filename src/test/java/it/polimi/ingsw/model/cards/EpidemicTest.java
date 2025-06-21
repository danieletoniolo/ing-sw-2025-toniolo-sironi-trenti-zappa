package it.polimi.ingsw.model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EpidemicTest {
    Epidemic card;
    Field cardLevelField = Card.class.getDeclaredField("level");
    Field idField = Card.class.getDeclaredField("ID");

    EpidemicTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        card = new Epidemic(2, 0);
        assertNotNull(card);
        cardLevelField.setAccessible(true);
        idField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        Epidemic c1 = new Epidemic();
        assertNotNull(c1);
        assertEquals(0, idField.get(c1));
        assertEquals(0, cardLevelField.get(c1));
        assertEquals(CardType.EPIDEMIC, c1.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, cardLevelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0,idField.get(card));

        Random rand = new Random();
        int id = rand.nextInt(3) + 1;
        Epidemic randomCard = new Epidemic(1, id);
        assertEquals(id, idField.get(randomCard));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.EPIDEMIC, card.getCardType());
    }
}