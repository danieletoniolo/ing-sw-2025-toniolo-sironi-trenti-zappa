package Model.Cards;

import Model.Good.Good;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;

class PlanetsTest {
    Planets card;
    Good[][] planets;

    @BeforeEach
    void setUp() {
        planets = new Good[][]{};
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

    @Test
    void getPlanets() {
        //...
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