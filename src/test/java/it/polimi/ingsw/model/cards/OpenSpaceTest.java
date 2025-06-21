package it.polimi.ingsw.model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceTest {
    OpenSpace card;
    Field levelField = Card.class.getDeclaredField("level");
    Field idField = Card.class.getDeclaredField("ID");

    OpenSpaceTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        card = new OpenSpace(2, 0);
        assertNotNull(card);
        levelField.setAccessible(true);
        idField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        OpenSpace c1 = new OpenSpace();
        assertNotNull(c1);
        assertEquals(0, idField.get(c1));
        assertEquals(0, levelField.get(c1));
        assertEquals(CardType.OPENSPACE, c1.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, levelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0, idField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        OpenSpace randomCard = new OpenSpace(1, id);
        assertEquals(id, idField.get(randomCard));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.OPENSPACE, card.getCardType());
    }
}