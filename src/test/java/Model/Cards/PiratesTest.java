package Model.Cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class PiratesTest {
    Pirates card;
    List<Hit> fires;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        fires.add(new Hit(HitType.HEAVYFIRE, Direction.NORTH));
        fires.add(new Hit(HitType.LIGHTFIRE, Direction.SOUTH));
        assertFalse(fires.contains(null));
        card = new Pirates(2, 0, fires, 4, 2, 1);
        assertNotNull(card, "Card variable not inialized correctly");
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(level, 0, fires, 4, 2, 1);
        assertEquals(level, randomCard.getCardLevel());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.PIRATES, card.getCardType());
    }

    @RepeatedTest(5)
    void getFire() {
        List<Hit> hits = card.getFires();
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
        hits = randomCard.getFires();
        assertEquals(randomHits, hits);
        assertEquals(checkHits, hits);
    }

    @RepeatedTest(5)
    void getCannonStrengthRequired() {
        assertEquals(2,card.getCannonStrengthRequired());

        Random random = new Random();
        int cannonStrength = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(2, 0, fires, 4, cannonStrength, 1);
        assertEquals(cannonStrength, randomCard.getCannonStrengthRequired());
    }

    @RepeatedTest(5)
    void getFlightDays() {
        assertEquals(1,card.getFlightDays());

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(2, 0, fires, 4, 2, flightDays);
        assertEquals(flightDays, randomCard.getFlightDays());
    }

    @RepeatedTest(5)
    void getCredit() {
        assertEquals(4, card.getCredit());

        Random random = new Random();
        int credit = random.nextInt(3) + 1;
        Pirates randomCard = new Pirates(2, 0, fires, credit, 2, 1);
        assertEquals(credit, randomCard.getCredit());
    }
}