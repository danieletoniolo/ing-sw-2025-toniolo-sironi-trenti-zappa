package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class AbandonedStationTest {
    AbandonedStation card;
    PlayerData player;
    List<Good> goods;

    @BeforeEach
    void setUp() {
        goods = new ArrayList<>();
        goods.add(new Good(GoodType.RED));
        goods.add(new Good(GoodType.BLUE));
        assertFalse(goods.contains(null));
        card = new AbandonedStation(2,3,1, goods);
        assertNotNull(card, "Card variable not initialized correctly");
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

    @Test
    void isPlayed(){
        assertFalse(card.isPlayed());
        card.apply(player);
        assertTrue(card.isPlayed());
    }

    @Test
    void getGoods() {
        List<Good> checkGoods = card.getGoods();
        assertEquals(goods,checkGoods);
        assertEquals(2, checkGoods.size());
        assertEquals(GoodType.RED,checkGoods.get(0).getColor());
        assertEquals(GoodType.BLUE,checkGoods.get(1).getColor());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSTATION, card.getCardType());
    }

    @Test
    void apply() {
        boolean[][] spots = {};
        SpaceShip ship = new SpaceShip(spots);
        player = new PlayerData("NAME", PlayerColor.BLUE, ship);
        assertNotNull(player);
        card.apply(player);
        assertTrue(card.isPlayed());
    }


    @RepeatedTest(10)
    void testRandomizedGoods() {
        // + 1 because the highest integer number doesn't belong to the interval of the randomized
        Random random = new Random();
        int crewRequired = random.nextInt(card.getCrewRequired()) + 1;
        int flightDays = random.nextInt(card.getCrewRequired()) + 1;
        List<Good> randomGoods = Collections.singletonList(new Good(GoodType.values()[random.nextInt(GoodType.values().length)]));

        AbandonedStation randomStation = new AbandonedStation(1, crewRequired, flightDays, randomGoods);
        assertEquals(crewRequired, randomStation.getCrewRequired());
        assertEquals(flightDays, randomStation.getFlightDays());
        assertEquals(randomGoods, randomStation.getGoods());
        assertFalse(randomStation.isPlayed());
    }
}