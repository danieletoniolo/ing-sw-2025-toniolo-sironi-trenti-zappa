package Model.Cards;

import Model.Cards.Hits.Hit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlaversTest {
    Slavers card;
    List<Hit> fires;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        card = new Slavers(1, 3, 2, 4, 5);
        assertNotNull(card, "Card variable not inialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());
    }

    @Test
    void getCannonStrengthRequired() {
        assertEquals(4,card.getFlightDays());
    }

    @Test
    void getFlightDays() {
        assertEquals(5,card.getFlightDays());
    }

    @Test
    void getReward() {
        assertEquals(3,card.getReward());
    }

    @Test
    void getCrewLost() {
        assertEquals(1,card.getCrewLost());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SLAVERS,card.getCardType());
    }

    @Test
    void apply() {
    }
}