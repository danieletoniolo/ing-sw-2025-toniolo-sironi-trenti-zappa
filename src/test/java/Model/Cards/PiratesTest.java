package Model.Cards;

import Model.Cards.Hits.Hit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class PiratesTest {
    Pirates card;
    List<Hit> fires;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        card = new Pirates(fires, 4, 2, 1, 3);
        assertNotNull(card, "Card variable not inialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());
    }

    @Test
    void getFire() {
        //...
    }

    @Test
    void getCannonStrengthRequired() {
        assertEquals(1,card.getFlightDays());
    }

    @Test
    void getFlightDays() {
        assertEquals(3,card.getFlightDays());
    }

    @Test
    void getCredit() {
        assertEquals(4, card.getCredit());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.PIRATES, card.getCardType());
    }

    @Test
    void apply() {
    }
}