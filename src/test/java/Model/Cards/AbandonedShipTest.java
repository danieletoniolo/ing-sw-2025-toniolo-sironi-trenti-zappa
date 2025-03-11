package Model.Cards;

import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipTest {
    AbandonedShip card;
    PlayerData player;
    Random random = new Random();

    @BeforeEach
    void setUp() {
        card = new AbandonedShip(2,3,3,2);
        assertNotNull(card);
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getCrewRequired() {
        assertEquals(3,card.getCrewRequired());
    }

    @Test
    void getFlightDays() {
        assertEquals(3,card.getFlightDays());
    }

    @Test
    void getCredit() {
        assertEquals(2,card.getCredit());
    }

    @Test
    void isPlayed(){
        assertFalse(card.isPlayed());
        card.apply(player);
        assertTrue(card.isPlayed());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.ABANDONEDSHIP, card.getCardType());
    }

    @Test
    void apply() {
        boolean[][] spots = {};
        SpaceShip ship = new SpaceShip(spots);
        player = new PlayerData("NAME", PlayerColor.BLUE, ship);
        assertNotNull(player);
        card.apply(player);
        assertTrue(card.isPlayed());
    }



    @ParameterizedTest
    @CsvSource({
            "1, 2, 3, 50",
            "2, 3, 5, 100",
            "3, 4, 6, 150"
    })
    void testCardInitialization(int level, int crew, int flightDays, int credit) {
        AbandonedShip testCard = new AbandonedShip(level, crew, flightDays, credit);
        assertEquals(level, testCard.getCardLevel());
        assertEquals(crew, testCard.getCrewRequired());
        assertEquals(flightDays, testCard.getFlightDays());
        assertEquals(credit, testCard.getCredit());
    }

    @RepeatedTest(5)
    void testRandomizedInitialization() {
        // + 1 because the highest integer number doesn't belong to the interval of the randomized
        int crew = random.nextInt(card.getCrewRequired()) + 1;
        int flightDays = random.nextInt(card.getFlightDays()) + 1;
        int credit = random.nextInt(card.getCredit()) + 1;

        AbandonedShip randomCard = new AbandonedShip(2, crew, flightDays, credit);
        System.out.println(randomCard.getCrewRequired() + " " + randomCard.getFlightDays() + " " + randomCard.getCredit());
        assertEquals(crew, randomCard.getCrewRequired());
        assertEquals(flightDays, randomCard.getFlightDays());
        assertEquals(credit, randomCard.getCredit());
    }
}