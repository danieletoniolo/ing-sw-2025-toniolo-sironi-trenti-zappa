package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class AbandonedStationTest {
    AbandonedStation card;
    List<Good> goods;
    Field levelField = Card.class.getDeclaredField("level");
    Field idField = Card.class.getDeclaredField("ID");
    Field crewField = AbandonedStation.class.getDeclaredField("crewRequired");
    Field flightField = AbandonedStation.class.getDeclaredField("flightDays");
    Field goodsField = AbandonedStation.class.getDeclaredField("goods");

    AbandonedStationTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        goods = new ArrayList<>();
        goods.add(new Good(GoodType.RED));
        goods.add(new Good(GoodType.BLUE));
        assertFalse(goods.contains(null));
        card = new AbandonedStation(2,3,1, 0, goods);
        assertNotNull(card);
        levelField.setAccessible(true);
        idField.setAccessible(true);
        crewField.setAccessible(true);
        flightField.setAccessible(true);
        goodsField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        AbandonedStation c1 = new AbandonedStation();
        assertNotNull(c1);
        assertEquals(0, idField.get(c1));
        assertEquals(0, levelField.get(c1));
        assertEquals(0, crewField.get(c1));
        assertEquals(0, flightField.get(c1));
        assertNull(goodsField.get(c1));
        assertEquals(CardType.ABANDONEDSTATION, c1.getCardType());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSTATION, card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, levelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(3, idField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        AbandonedStation randomCard = new AbandonedStation(2, id, 1, 0, goods);
        assertEquals(id, idField.get(randomCard));
    }

    @RepeatedTest(5)
    void getCrewRequired() throws IllegalAccessException {
        assertEquals(1, crewField.get(card));

        Random random = new Random();
        int crew = random.nextInt(3) + 1;
        AbandonedStation randomCard = new AbandonedStation(2, 3, crew, 0, goods);
        assertEquals(crew, crewField.get(randomCard));
    }

    @RepeatedTest(5)
    void getFlightDays() throws IllegalAccessException {
        assertEquals(0, flightField.get(card));

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        AbandonedStation randomCard = new AbandonedStation(2, 3, 1, flightDays, goods);
        assertEquals(flightDays, flightField.get(randomCard));
    }

    @RepeatedTest(5)
    void getGoods() throws IllegalAccessException {
        List<Good> checkGoods = (List<Good>) goodsField.get(card);
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
        assertEquals(randomGoods, goodsField.get(randomCard));
        assertEquals(goods,goodsField.get(randomCard));
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