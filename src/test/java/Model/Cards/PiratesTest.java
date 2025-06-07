package Model.Cards;

import it.polimi.ingsw.model.cards.CardType;
import it.polimi.ingsw.model.cards.hits.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Pirates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class PiratesTest {
    Pirates card;
    List<Hit> fires;
    Field cannonStrengthRequiredField = Pirates.class.getDeclaredField("cannonStrengthRequired");
    Field flightDaysField = Pirates.class.getDeclaredField("flightDays");
    Field firesField = Pirates.class.getDeclaredField("fires");
    Field creditField = Pirates.class.getDeclaredField("credit");
    Field idField = Card.class.getDeclaredField("ID");
    Field levelField = Card.class.getDeclaredField("level");

    PiratesTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        fires.add(new Hit(HitType.HEAVYFIRE, Direction.NORTH));
        fires.add(new Hit(HitType.LIGHTFIRE, Direction.SOUTH));
        assertFalse(fires.contains(null));
        card = new Pirates(2, 0, fires, 4, 2, 1);
        assertNotNull(card);
        cannonStrengthRequiredField.setAccessible(true);
        flightDaysField.setAccessible(true);
        firesField.setAccessible(true);
        creditField.setAccessible(true);
        idField.setAccessible(true);
        levelField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        Pirates c1 = new Pirates();
        assertNotNull(c1);
        assertEquals(0, idField.get(c1));
        assertEquals(0, levelField.get(c1));
        assertEquals(0, creditField.get(c1));
        assertEquals(0, cannonStrengthRequiredField.get(c1));
        assertEquals(0, flightDaysField.get(c1));
        assertNull(firesField.get(c1));
        assertEquals(CardType.PIRATES, c1.getCardType());
    }

    @Test
    void testFiresEmptyOrNull() {
        assertThrows(NullPointerException.class, () -> new Pirates(2, 3, null, 4, 2, 1));
        assertThrows(NullPointerException.class, () -> new Pirates(2, 3, new ArrayList<>(), 4, 2, 1));
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, levelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0, idField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(1, id, fires, 4, 2, 1);
        assertEquals(id, idField.get(randomCard));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.PIRATES, card.getCardType());
    }

    @RepeatedTest(5)
    void getFire() throws IllegalAccessException {
        List<Hit> hits = (List<Hit>) firesField.get(card);
        assertEquals(fires,hits);
        assertEquals(2, hits.size());
        assertEquals(HitType.HEAVYFIRE, hits.get(0).getType());
        assertEquals(HitType.LIGHTFIRE, hits.get(1).getType());
        assertEquals(Direction.NORTH, hits.get(0).getDirection());
        assertEquals(Direction.SOUTH, hits.get(1).getDirection());

        Random random = new Random();
        List<Hit> randomHits = new ArrayList<>();
        List<Hit> checkHits = new ArrayList<>();
        HitType[] valuesHitType = HitType.values();
        Direction[] valuesDirection = Direction.values();
        for (int i = 0; i < 2; i++) {
            randomHits.add(new Hit(valuesHitType[random.nextInt(valuesHitType.length)], valuesDirection[random.nextInt(valuesDirection.length)]));
            checkHits.add(randomHits.get(i));
        }
        Pirates randomCard = new Pirates(2, 0, randomHits, 4, 2, 1);
        hits = (List<Hit>) firesField.get(randomCard);
        assertEquals(randomHits, hits);
        assertEquals(checkHits, hits);
    }

    @RepeatedTest(5)
    void getCannonStrengthRequired() throws IllegalAccessException {
        assertEquals(2, cannonStrengthRequiredField.get(card));

        Random random = new Random();
        int cannonStrength = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(2, 0, fires, 4, cannonStrength, 1);
        assertEquals(cannonStrength, cannonStrengthRequiredField.get(randomCard));
    }

    @RepeatedTest(5)
    void getFlightDays() throws IllegalAccessException {
        assertEquals(1, flightDaysField.get(card));

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(2, 0, fires, 4, 2, flightDays);
        assertEquals(flightDays, flightDaysField.get(randomCard));
    }

    @RepeatedTest(5)
    void getCredit() throws IllegalAccessException {
        assertEquals(4, creditField.get(card));

        Random random = new Random();
        int credit = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(2, 0, fires, credit, 2, 1);
        assertEquals(credit, creditField.get(randomCard));
    }
}