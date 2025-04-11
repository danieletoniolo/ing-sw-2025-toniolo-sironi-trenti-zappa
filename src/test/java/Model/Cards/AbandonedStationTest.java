package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class AbandonedStationTest {
    AbandonedStation card;
    List<Good> goods;

    @BeforeEach
    void setUp() {
        goods = new ArrayList<>();
        goods.add(new Good(GoodType.RED));
        goods.add(new Good(GoodType.BLUE));
        assertFalse(goods.contains(null));
        card = new AbandonedStation(2,3,1, 0, goods);
        assertNotNull(card, "Card variable not initialized correctly");
    }

    @Test
    void testConstructor() {
        AbandonedStation c1 = new AbandonedStation();
        assertNotNull(c1);
        assertEquals(0, c1.getID());
        assertEquals(0, c1.getCardLevel());
        assertEquals(0, c1.getCrewRequired());
        assertEquals(0, c1.getFlightDays());
        assertNull(c1.getGoods());
        assertEquals(CardType.ABANDONEDSTATION, c1.getCardType());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSTATION, card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        AbandonedStation randomCard = new AbandonedStation(level, 3, 1, 0, goods);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(3,card.getID());

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        AbandonedStation randomCard = new AbandonedStation(2, id, 1, 0, goods);
        assertEquals(id, randomCard.getID());
    }

    @RepeatedTest(5)
    void getCrewRequired() {
        assertEquals(1,card.getCrewRequired());

        Random random = new Random();
        int crew = random.nextInt(3) + 1;
        AbandonedStation randomCard = new AbandonedStation(2, 3, crew, 0, goods);
        assertEquals(crew, randomCard.getCrewRequired());
    }

    @RepeatedTest(5)
    void getFlightDays() {
        assertEquals(0,card.getFlightDays());

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        AbandonedStation randomCard = new AbandonedStation(2, 3, 1, flightDays, goods);
        assertEquals(flightDays, randomCard.getFlightDays());
    }

    @RepeatedTest(5)
    void getGoods() {
        List<Good> checkGoods = card.getGoods();
        assertEquals(goods,checkGoods);
        assertEquals(2, checkGoods.size());
        assertEquals(GoodType.RED,checkGoods.get(0).getColor());
        assertEquals(GoodType.BLUE,checkGoods.get(1).getColor());

        Random random = new Random();
        GoodType[] values = GoodType.values();
        List<Good> randomGoods = new ArrayList<>();
        List<Good> goods = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            randomGoods.add(new Good(values[random.nextInt(values.length)]));
            goods.add(randomGoods.get(i));
        }
        AbandonedStation randomCard = new AbandonedStation(2, 3, 1, 0, randomGoods);
        assertEquals(randomGoods, randomCard.getGoods());
        assertEquals(goods,randomCard.getGoods());
    }

    @RepeatedTest(5)
    void constructor_nullGoods_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AbandonedStation(2, 3, 1, 0, null));
    }

    @RepeatedTest(5)
    void constructor_emptyGoods_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AbandonedStation(2, 3, 1, 0, new ArrayList<>()));
    }
}