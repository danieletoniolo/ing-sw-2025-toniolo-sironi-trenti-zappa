package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;

class PlanetsTest {
    Planets card;
    Good[][] planets;

    @BeforeEach
    void setUp() {
        planets = new Good[][]{
                {new Good(GoodType.BLUE), new Good(GoodType.RED)},
                {new Good(GoodType.YELLOW)},
        };
        card = new Planets(2, 0, planets, 3);
        assertNotNull(card);
    }

    @Test
    void getCardType() {
        assertEquals(CardType.PLANETS, card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        Planets randomCard = new Planets(level, 0, planets, 3);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getPlanetNumbers() {
        assertEquals(planets.length, card.getPlanetNumbers());

        Random random = new Random();
        int numberOfPlanets = random.nextInt(3) + 1;
        Good[][] randomPlanets = new Good[numberOfPlanets][8];
        for (int i = 0; i < numberOfPlanets; i++) {
            int numberOfGoods = random.nextInt(3) + 1;
            for (int j = 0; j < numberOfGoods; j++) {
                randomPlanets[i][j] = new Good(GoodType.BLUE);
            }
        }
        Planets randomCard = new Planets(2, 0, randomPlanets, 3);
        assertEquals(numberOfPlanets, randomCard.getPlanetNumbers());
    }

    @RepeatedTest(5)
    void getFlightDays() {
        assertEquals(3, card.getFlightDays());

        Random random = new Random();
        int days = random.nextInt(3) + 1;
        Planets randomCard = new Planets(2, 0, planets, days);
        assertEquals(days, randomCard.getFlightDays());
    }

    @RepeatedTest(5)
    void getPlanet() {
        List<Good> check = card.getPlanet(0);
        assertEquals(2, check.size());
        assertEquals(GoodType.BLUE, check.get(0).getColor());
        assertEquals(GoodType.RED, check.get(1).getColor());

        List<Good> check1 = card.getPlanet(1);
        assertEquals(1, check1.size());
        assertEquals(GoodType.YELLOW, check1.getFirst().getColor());

        assertThrows(IndexOutOfBoundsException.class, () -> card.getPlanet(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> card.getPlanet(2));


        Random random = new Random();
        int numberOfPlanets = random.nextInt(3) + 1;
        Good[][] randomPlanets = new Good[numberOfPlanets][8];
        GoodType[] values = GoodType.values();

        for (int i = 0; i < numberOfPlanets; i++) {
            int numberOfGoods = random.nextInt(3) + 1;
            for (int j = 0; j < numberOfGoods; j++) {
                GoodType randomColor = values[random.nextInt(values.length)];
                Good addGood = new Good(randomColor);
                randomPlanets[i][j] = addGood;
            }
        }
        Planets randomCard = new Planets(2, 0, randomPlanets, 3);
        for (int i = 0; i < numberOfPlanets; i++) {
            List<Good> checkRandom = randomCard.getPlanet(i);
            assertEquals(randomPlanets[i].length, checkRandom.size());
            for (int j = 0; j < randomPlanets[i].length; j++) {
                if(randomPlanets[i][j] != null){
                    assertEquals(randomPlanets[i][j].getColor(), checkRandom.get(j).getColor());
                }
            }
        }
    }
}