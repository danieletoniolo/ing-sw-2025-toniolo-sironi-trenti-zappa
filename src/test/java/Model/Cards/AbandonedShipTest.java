package Model.Cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipTest {
    AbandonedShip card;

    @BeforeEach
    void setUp() {
        card = new AbandonedShip(2,3,3,2, 0);
        assertNotNull(card);
    }

    @RepeatedTest(5)
    void getCrewRequired() {
        assertEquals(3,card.getCrewRequired());

        Random random = new Random();
        int crew = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, 3, crew, 2, 0);
        assertEquals(crew, randomCard.getCrewRequired());
    }

    @RepeatedTest(5)
    void getFlightDays() {
        assertEquals(2,card.getFlightDays());

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, 3, 2, flightDays, 0);
        assertEquals(flightDays, randomCard.getFlightDays());
    }

    @RepeatedTest(5)
    void getCredit() {
        assertEquals(0,card.getCredit());

        Random random = new Random();
        int credit = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, 3, 3, 2, credit);
        assertEquals(credit, randomCard.getCredit());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSHIP, card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(level, 3, 3, 2, 0);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(3,card.getID());

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        AbandonedShip randomCard = new AbandonedShip(2, id, 3, 2, 0);
        assertEquals(id, randomCard.getID());
    }
}