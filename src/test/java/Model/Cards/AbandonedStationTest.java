package Model.Cards;

import Model.Good.Good;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

class AbandonedStationTest {
    AbandonedStation card;
    //PlayerData player;
    List<Good> goods;

    @BeforeEach
    void setUp() {
        goods = new ArrayList<>();
        card = new AbandonedStation(2,3,1, goods);
        assertNotNull(card, "Card variable not initialized correctly");

        //player = new PlayerData();
        //assertNotNull(player, "Player variable not initialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getCrewRequired() {
        assertEquals(3,card.getCrewRequired());
    }

    @Test
    void getFlightDays() {
        assertEquals(1,card.getFlightDays());
    }

    //isPlayed (?)

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSTATION, card.getCardType());
    }

    @Test
    void apply() {
    }
}