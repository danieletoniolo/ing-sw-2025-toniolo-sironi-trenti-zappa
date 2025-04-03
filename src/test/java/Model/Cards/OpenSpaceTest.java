package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceTest {
    OpenSpace card;

    @BeforeEach
    void setUp() {
        card = new OpenSpace(2, 0);
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        OpenSpace randomCard = new OpenSpace(level, 0);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(0,card.getID());

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        OpenSpace randomCard = new OpenSpace(1, id);
        assertEquals(id, randomCard.getID());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.OPENSPACE, card.getCardType());
    }
}