package Model.Cards;

import Model.Cards.Hits.Hit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class CombatZoneTest {
    CombatZone card;
    List<Hit> fires;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        card = new CombatZone(2, 3, fires, 2);
        assertNotNull(card, "Card variable not initialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getFlightDays() {
        assertEquals(2,card.getFlightDays());
    }

    @Test
    void getLost() {
        assertEquals(3,card.getLost());
    }

    // getFires()  (?)

    @Test
    void getCardType() {
        assertEquals(CardType.COMBATZONE, card.getCardType());
    }

    @Test
    void apply() {
    }
}