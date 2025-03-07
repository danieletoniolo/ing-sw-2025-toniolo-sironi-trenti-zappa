package Model.Cards;

import Model.Good.Good;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class SmugglersTest {
    Smugglers card;
    List<Good> rewards;

    @BeforeEach
    void setUp() {
        rewards = new ArrayList<>();
        card = new Smugglers(rewards, 1, 2, 3, 4);
        assertNotNull(card, "Card variable not initialized correctly");
    }

    @Test
    void getGoodsReward() {
        //...
    }

    @Test
    void getGoodsLoss() {
        assertEquals(1, card.getGoodsLoss());
    }

    @Test
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());
    }

    @Test
    void getCannonStrengthRequired() {
        assertEquals(3,card.getCannonStrengthRequired());
    }

    @Test
    void getFlightDays() {
        assertEquals(4,card.getFlightDays());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SMUGGLERS, card.getCardType());
    }

    @Test
    void apply() {
    }
}