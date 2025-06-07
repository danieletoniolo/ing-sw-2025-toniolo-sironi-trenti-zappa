package Model.Cards;

import it.polimi.ingsw.model.cards.hits.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardType;
import it.polimi.ingsw.model.cards.Slavers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SlaversTest {
    Slavers card;
    List<Hit> fires;
    Field cannonStrengthRequiredField = Slavers.class.getDeclaredField("cannonStrengthRequired");
    Field flightDaysField = Slavers.class.getDeclaredField("flightDays");
    Field crewLostField = Slavers.class.getDeclaredField("crewLost");
    Field creditField = Slavers.class.getDeclaredField("credit");
    Field IDField = Card.class.getDeclaredField("ID");
    Field cardLevelField = Card.class.getDeclaredField("level");

    SlaversTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        card = new Slavers(1, 0, 3, 2, 4, 5);
        assertNotNull(card);
        cannonStrengthRequiredField.setAccessible(true);
        flightDaysField.setAccessible(true);
        crewLostField.setAccessible(true);
        creditField.setAccessible(true);
        IDField.setAccessible(true);
        cardLevelField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        Slavers c1 = new Slavers();
        assertNotNull(c1);
        assertEquals(0, IDField.get(c1));
        assertEquals(0, cardLevelField.get(c1));
        assertEquals(0, cannonStrengthRequiredField.get(c1));
        assertEquals(0, flightDaysField.get(c1));
        assertEquals(0, creditField.get(c1));
        assertEquals(0, crewLostField.get(c1));
        assertEquals(CardType.SLAVERS, c1.getCardType());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SLAVERS,card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(1, cardLevelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0, IDField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, id, 3, 2, 4, 5);
        assertEquals(id, IDField.get(randomCard));
    }

    @RepeatedTest(5)
    void getCannonStrengthRequired() throws IllegalAccessException {
        assertEquals(4, cannonStrengthRequiredField.get(card));

        Random random = new Random();
        int cannonStrength = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, 3, 4, cannonStrength, 5);
        assertEquals(cannonStrength, cannonStrengthRequiredField.get(randomCard));
    }

    @RepeatedTest(5)
    void getFlightDays() throws IllegalAccessException {
        assertEquals(5, flightDaysField.get(card));

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, 3, 2, 4, flightDays);
        assertEquals(flightDays, flightDaysField.get(randomCard));
    }

    @RepeatedTest(5)
    void getCredit() throws IllegalAccessException {
        assertEquals(2, creditField.get(card));

        Random random = new Random();
        int credit = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, 3, credit, 4, 5);
        assertEquals(credit, creditField.get(randomCard));
    }

    @RepeatedTest(5)
    void getCrewLost() throws IllegalAccessException {
        assertEquals(3, crewLostField.get(card));

        Random random = new Random();
        int crewLost = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, crewLost, 2, 4, 5);
        assertEquals(crewLost, crewLostField.get(randomCard));
    }
}