package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StarDustTest {
    StarDust card;

    @BeforeEach
    void setUp() {
        card = new StarDust(2);
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
}