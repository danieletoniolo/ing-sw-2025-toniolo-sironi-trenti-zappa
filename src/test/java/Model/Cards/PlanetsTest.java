package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PlanetsTest {
    Planets card;
    Good[][] planets;

    @BeforeEach
    void setUp() {
        planets = new Good[][]{
                {new Good(GoodType.BLUE), new Good(GoodType.RED)},
                {new Good(GoodType.YELLOW), new Good(GoodType.GREEN)},
        };
        card = new Planets(2, planets, 3);
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getPlanetNumbers() {
        assertEquals(planets.length, card.getPlanetNumbers());
    }

    @ParameterizedTest
    @CsvSource({"-1,0,1"})
    void getPlanet(int n) {
        List<Good> check=null;
        if(n < 0){
            assertNull(check);
        } else {

            check = card.getPlanet(0);
            assertEquals(2, check.size());
            assertEquals(GoodType.BLUE, check.get(0).getColor());
            assertEquals(GoodType.RED, check.get(1).getColor());

            List<Good> check1 = card.getPlanet(1);
            assertEquals(2, check1.size());
            assertEquals(GoodType.YELLOW, check1.get(0).getColor());
            assertEquals(GoodType.GREEN, check1.get(1).getColor());
        }
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