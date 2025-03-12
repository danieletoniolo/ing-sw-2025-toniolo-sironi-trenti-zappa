package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PlanetsTest {
    Planets card;
    Good[][] planets;

    @BeforeEach
    void setUp() {
        planets = new Good[][]{
                {new Good(GoodType.BLUE), new Good(GoodType.RED)},
                {new Good(GoodType.YELLOW)},
        };
        card = new Planets(2, planets, 3);
        assertNotNull(card);
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getPlanetNumbers() {
        assertEquals(planets.length, card.getPlanetNumbers());
    }

    @Test
    void getPlanet() {
        List<Good> check = card.getPlanet(0);
        assertEquals(2, check.size());
        assertEquals(GoodType.BLUE, check.get(0).getColor());
        assertEquals(GoodType.RED, check.get(1).getColor());

        List<Good> check1 = card.getPlanet(1);
        assertEquals(1, check1.size());
        assertEquals(GoodType.YELLOW, check1.getFirst().getColor());
    }

    @Test
    void testGetPlanetValid() {
        List<Good> goods = card.getPlanet(1);
        assertNotNull(goods);
        assertEquals(1, goods.size());
    }

    @Test
    void testGetPlanetInvalidIndex() {
        assertNull(card.getPlanet(-1));
        assertNull(card.getPlanet(3));
    }

    @Test
    void testGetPlanetTwice() {
        assertNotNull(card.getPlanet(0));
        assertNull(card.getPlanet(0));
    }

    @Test
    void getFlightDays() {
        assertEquals(3, card.getFlightDays());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.PLANETS, card.getCardType());
    }

    @Test
    void apply() {

    }
}