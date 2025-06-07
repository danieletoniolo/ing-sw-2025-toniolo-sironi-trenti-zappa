package Model.Cards;

import it.polimi.ingsw.model.good.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardType;
import it.polimi.ingsw.model.cards.Planets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class PlanetsTest {
    Planets card;
    List<List<Good>> planets = Arrays.asList( Arrays.asList(new Good(GoodType.BLUE), new Good(GoodType.RED)),
            List.of(new Good(GoodType.YELLOW)));
    Field planetsField = Planets.class.getDeclaredField("planets");
    Field flightDaysField = Planets.class.getDeclaredField("flightDays");
    Field cardLevelField = Card.class.getDeclaredField("level");
    Field cardIDField = Card.class.getDeclaredField("ID");

    PlanetsTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        card = new Planets(2, 0, planets, 3);
        assertNotNull(card);
        planetsField.setAccessible(true);
        flightDaysField.setAccessible(true);
        cardLevelField.setAccessible(true);
        cardIDField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        Planets c1 = new Planets();
        assertNotNull(c1);
        assertEquals(0, cardIDField.get(c1));
        assertEquals(0, cardLevelField.get(c1));
        assertEquals(0, flightDaysField.get(c1));
        assertNull(planetsField.get(c1));
        assertEquals(CardType.PLANETS, c1.getCardType());
    }

    @Test
    void testPlanetsEmptyOrNull() {
        assertThrows(NullPointerException.class, () -> new Planets(2, 3, null, 3));
        assertThrows(NullPointerException.class, () -> new Planets(2, 3, new ArrayList<>(), 3));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.PLANETS, card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, cardLevelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0, cardIDField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        Planets randomCard = new Planets(1, id, planets, 3);
        assertEquals(id, cardIDField.get(randomCard));
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
    void getFlightDays() throws IllegalAccessException {
        assertEquals(3, flightDaysField.get(card));

        Random random = new Random();
        int days = random.nextInt(3) + 1;
        Planets randomCard = new Planets(2, 0, planets, days);
        assertEquals(days, flightDaysField.get(randomCard));
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