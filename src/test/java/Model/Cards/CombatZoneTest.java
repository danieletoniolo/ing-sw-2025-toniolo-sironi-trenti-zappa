package Model.Cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;

class CombatZoneTest {
    CombatZone card;
    ArrayList<Hit> fires;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        fires.add(new Hit(HitType.HEAVYFIRE, Direction.EAST));
        fires.add(new Hit(HitType.LARGEMETEOR, Direction.NORTH));
        assertFalse(fires.contains(null));
        card = new CombatZone(2, 3, fires, 2, 0);
        assertNotNull(card);
    }

    @Test
    void testConstructor() {
        CombatZone c1 = new CombatZone();
        assertNotNull(c1);
        assertEquals(0, c1.getID());
        assertEquals(0, c1.getCardLevel());
        assertEquals(0, c1.getFlightDays());
        assertEquals(0, c1.getLost());
        assertNull(c1.getFires());
        assertEquals(CardType.COMBATZONE, c1.getCardType());
    }

    @Test
    void testFiresEmptyOrNull() {
        assertThrows(NullPointerException.class, () -> new CombatZone(2, 3, null, 2, 0));
        assertThrows(NullPointerException.class, () -> new CombatZone(2, 3, new ArrayList<>(), 2, 0));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.COMBATZONE, card.getCardType());
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        CombatZone randomCard = new CombatZone(2, 3, fires, level, 0);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(0,card.getID());

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        CombatZone randomCard = new CombatZone(2, 3, fires, 2, id);
        assertEquals(id, randomCard.getID());
    }

    @RepeatedTest(5)
    void getFlightDays() {
        assertEquals(2,card.getFlightDays());

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        CombatZone randomCard = new CombatZone(flightDays, 3, fires, 2, 0);
        assertEquals(flightDays, randomCard.getFlightDays());
    }

    @RepeatedTest(5)
    void getLost() {
        assertEquals(3,card.getLost());

        Random random = new Random();
        int lost = random.nextInt(3) + 1;
        CombatZone randomCard = new CombatZone(2, lost, fires, 2, 0);
        assertEquals(lost, randomCard.getLost());
    }

    @RepeatedTest(5)
    void getFires(){
        ArrayList<Hit> hits = card.getFires();
        assertEquals(fires, hits);
        assertEquals(2,hits.size());
        assertEquals(HitType.HEAVYFIRE,hits.get(0).getType());
        assertEquals(HitType.LARGEMETEOR,hits.get(1).getType());
        assertEquals(Direction.EAST,hits.get(0).getDirection());
        assertEquals(Direction.NORTH,hits.get(1).getDirection());

        Random random = new Random();
        ArrayList<Hit> randomHits = new ArrayList<>();
        ArrayList<Hit> fires = new ArrayList<>();
        HitType[] valuesHitType = HitType.values();
        Direction[] valuesDirection = Direction.values();
        for (int i = 0; i < 2; i++) {
            randomHits.add(new Hit(valuesHitType[random.nextInt(valuesHitType.length)], valuesDirection[random.nextInt(valuesDirection.length)]));
            fires.add(randomHits.get(i));
        }

        CombatZone randomCard = new CombatZone(2, 3, randomHits, 2, 0);
        hits = randomCard.getFires();
        assertEquals(randomHits, hits);
        assertEquals(fires, hits);
    }
}