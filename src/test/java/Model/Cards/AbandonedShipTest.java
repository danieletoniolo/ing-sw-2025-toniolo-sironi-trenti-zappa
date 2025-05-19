package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipTest {
    AbandonedShip card;
    Field crewRequiredField = AbandonedShip.class.getDeclaredField("crewRequired");
    Field flightDaysField = AbandonedShip.class.getDeclaredField("flightDays");
    Field creditField = AbandonedShip.class.getDeclaredField("credit");
    Field cardLevelField = Card.class.getDeclaredField("level");
    Field cardIDField = Card.class.getDeclaredField("ID");

    AbandonedShipTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        card = new AbandonedShip(2,3,3,2, 0);
        assertNotNull(card);
        crewRequiredField.setAccessible(true);
        flightDaysField.setAccessible(true);
        creditField.setAccessible(true);
        cardLevelField.setAccessible(true);
        cardIDField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        AbandonedShip c1 = new AbandonedShip();
        assertNotNull(c1);
        assertEquals(0, cardIDField.get(c1));
        assertEquals(0, cardLevelField.get(c1));
        assertEquals(0, crewRequiredField.get(c1));
        assertEquals(0, flightDaysField.get(c1));
        assertEquals(0, creditField.get(c1));
        assertEquals(CardType.ABANDONEDSHIP, c1.getCardType());
    }

    @RepeatedTest(5)
    void getCrewRequired() throws IllegalAccessException {
        assertEquals(3,crewRequiredField.get(card));

        Random random = new Random();
        int crew = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, 3, crew, 2, 0);
        assertEquals(crew, crewRequiredField.get(randomCard));
    }

    @RepeatedTest(5)
    void getFlightDays() throws IllegalAccessException {
        assertEquals(2,flightDaysField.get(card));

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, 3, 2, flightDays, 0);
        assertEquals(flightDays, flightDaysField.get(randomCard));
    }

    @RepeatedTest(5)
    void getCredit() throws IllegalAccessException {
        assertEquals(0,creditField.get(card));

        Random random = new Random();
        int credit = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, 3, 3, 2, credit);
        assertEquals(credit, creditField.get(randomCard));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSHIP, card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2,cardLevelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(3,cardIDField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, id, 3, 2, 0);
        assertEquals(id, cardIDField.get(randomCard));
    }
}