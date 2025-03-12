package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceTest {
    OpenSpace card;

    @BeforeEach
    void setUp() {
        card = new OpenSpace(2);
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.OPENSPACE, card.getCardType());
    }

    @Test
    void apply() {

    }

    @ParameterizedTest
    @CsvSource({"1", "5", "10"})
    void testCardLevel(int level) {
        OpenSpace openSpace = new OpenSpace(level);
        assertEquals(level, openSpace.getCardLevel());
    }
}