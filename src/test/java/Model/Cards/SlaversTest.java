package Model.Cards;

import Model.Cards.Hits.Hit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SlaversTest {
    Slavers card;
    List<Hit> fires;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        card = new Slavers(1, 0, 3, 2, 4, 5);
        assertNotNull(card, "Card variable not inialized correctly");
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SLAVERS,card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(1, card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(level, 0, 3, 2, 4, 5);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(0, card.getID());

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, id, 3, 2, 4, 5);
        assertEquals(id, randomCard.getID());
    }

    @RepeatedTest(5)
    void getCannonStrengthRequired() {
        assertEquals(4,card.getCannonStrengthRequired());

        Random random = new Random();
        int cannonStrength = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, 3, 4, cannonStrength, 5);
        assertEquals(cannonStrength, randomCard.getCannonStrengthRequired());
    }

    @RepeatedTest(5)
    void getFlightDays() {
        assertEquals(5,card.getFlightDays());

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, 3, 2, 4, flightDays);
        assertEquals(flightDays, randomCard.getFlightDays());
    }

    @RepeatedTest(5)
    void getCredit() {
        assertEquals(2,card.getCredit());

        Random random = new Random();
        int credit = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, 3, credit, 4, 5);
        assertEquals(credit, randomCard.getCredit());
    }

    @RepeatedTest(5)
    void getCrewLost() {
        assertEquals(3,card.getCrewLost());

        Random random = new Random();
        int crewLost = random.nextInt(3) + 1;
        Slavers randomCard = new Slavers(1, 0, crewLost, 2, 4, 5);
        assertEquals(crewLost, randomCard.getCrewLost());
    }
}