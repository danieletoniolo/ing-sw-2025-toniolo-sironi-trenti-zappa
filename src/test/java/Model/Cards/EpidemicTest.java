package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpidemicTest {
    Epidemic card;

    @BeforeEach
    void setUp() {
        card = new Epidemic(2);
        assertNotNull(card, "Card variable not initialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.EPIDEMIC, card.getCardType());
    }

    @Test
    void apply() {
    }
}