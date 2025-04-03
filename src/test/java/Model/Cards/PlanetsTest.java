package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class PlanetsTest {
    Planets card;
    List<List<Good>> planets = Arrays.asList( Arrays.asList(new Good(GoodType.BLUE), new Good(GoodType.RED)),
            Arrays.asList(new Good(GoodType.YELLOW)) );

    @BeforeEach
    void setUp() {
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
    void getCardID() {
        assertEquals(0,card.getID());

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        Planets randomCard = new Planets(1, id, planets, 3);
        assertEquals(id, randomCard.getID());
    }

    @RepeatedTest(5)
    void getPlanetNumbers() {
        assertEquals(planets.size(), card.getPlanetNumbers());

        Random random = new Random();
        int numberOfPlanets = random.nextInt(3) + 1;
        List<List<Good>> randomPlanets = new ArrayList<>();
        for (int i = 0; i < numberOfPlanets; i++) {
            randomPlanets.add(new ArrayList<>(List.of()));
            int numberOfGoods = random.nextInt(1, 5);
            for (int j = 0; j < numberOfGoods; j++) {
                randomPlanets.get(i).add(new Good(GoodType.BLUE));
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
        List<List<Good>> randomPlanets = new ArrayList<>();
        GoodType[] values = GoodType.values();

        for (int i = 0; i < numberOfPlanets; i++) {
            randomPlanets.add(new ArrayList<>(List.of()));
            int numberOfGoods = random.nextInt(3) + 1;
            for (int j = 0; j < numberOfGoods; j++) {
                GoodType randomColor = values[random.nextInt(values.length)];
                Good addGood = new Good(randomColor);
                randomPlanets.get(i).add(addGood);
            }
        }
        Planets randomCard = new Planets(2, 0, randomPlanets, 3);
        for (int i = 0; i < numberOfPlanets; i++) {
            List<Good> checkRandom = randomCard.getPlanet(i);
            assertEquals(randomPlanets.get(i).size(), checkRandom.size());
            for (int j = 0; j < randomPlanets.get(i).size(); j++) {
                if(randomPlanets.get(i).get(j) != null){
                    assertEquals(randomPlanets.get(i).get(j).getColor(), checkRandom.get(j).getColor());
                }
            }
        }
    }
}