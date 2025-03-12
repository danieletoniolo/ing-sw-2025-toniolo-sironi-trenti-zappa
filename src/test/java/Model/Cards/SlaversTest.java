package Model.Cards;

import Model.Cards.Hits.Hit;
import Model.Player.PlayerData;
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
    PlayerData player;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        card = new Slavers(1, 3, 2, 4, 5);
        assertNotNull(card, "Card variable not inialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());
    }

    @Test
    void getCannonStrengthRequired() {
        assertEquals(4,card.getCannonStrengthRequired());
    }

    @Test
    void getFlightDays() {
        assertEquals(5,card.getFlightDays());
    }

    @Test
    void getReward() {
        assertEquals(3,card.getReward());
    }

    @Test
    void getCrewLost() {
        assertEquals(1,card.getCrewLost());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SLAVERS,card.getCardType());
    }

    @Test
    void apply() {
    }

    @Test
    void isPlayed(){
        assertFalse(card.isPlayed());
        card.apply(player);
        //assertTrue(card.isPlayed());
    }


    @RepeatedTest(3)
    void testRandomCrewLostAndReward() {
        Random random = new Random();
        int crewLost = random.nextInt(card.getCrewLost()) + 1;
        int credit = random.nextInt(card.getReward()) + 1;
        Slavers slavers = new Slavers(crewLost, credit, 1, 4, 3);
        assertEquals(crewLost, slavers.getCrewLost());
        assertEquals(credit, slavers.getReward());
    }
}