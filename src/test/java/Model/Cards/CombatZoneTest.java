package Model.Cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

class CombatZoneTest {
    CombatZone card;
    ArrayList<Hit> fires;
    Field flightDaysField = CombatZone.class.getDeclaredField("flightDays");
    Field lostField = CombatZone.class.getDeclaredField("lost");
    Field firesField = CombatZone.class.getDeclaredField("fires");
    Field cardLevelField = Card.class.getDeclaredField("level");
    Field cardIDField = Card.class.getDeclaredField("ID");

    CombatZoneTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        fires.add(new Hit(HitType.HEAVYFIRE, Direction.EAST));
        fires.add(new Hit(HitType.LARGEMETEOR, Direction.NORTH));
        assertFalse(fires.contains(null));
        card = new CombatZone(2, 3, fires, 2, 0);
        assertNotNull(card);
        flightDaysField.setAccessible(true);
        lostField.setAccessible(true);
        firesField.setAccessible(true);
        cardLevelField.setAccessible(true);
        cardIDField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        CombatZone c1 = new CombatZone();
        assertNotNull(c1);
        assertEquals(0, cardIDField.get(c1));
        assertEquals(0, cardLevelField.get(c1));
        assertEquals(0, flightDaysField.get(c1));
        assertEquals(0, lostField.get(c1));
        assertNull(firesField.get(c1));
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
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, cardLevelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0,cardIDField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        CombatZone randomCard = new CombatZone(2, 3, fires, 2, id);
        assertEquals(id, cardIDField.get(randomCard));
    }

    @RepeatedTest(5)
    void getFlightDays() throws IllegalAccessException {
        assertEquals(2, flightDaysField.get(card));

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        CombatZone randomCard = new CombatZone(flightDays, 3, fires, 2, 0);
        assertEquals(flightDays, flightDaysField.get(randomCard));
    }

    @RepeatedTest(5)
    void getLost() throws IllegalAccessException {
        assertEquals(3, lostField.get(card));

        Random random = new Random();
        int lost = random.nextInt(3) + 1;
        CombatZone randomCard = new CombatZone(2, lost, fires, 2, 0);
        assertEquals(lost, lostField.get(randomCard));
    }

    @RepeatedTest(5)
    void getFires() throws IllegalAccessException {
        ArrayList<Hit> hits = (ArrayList<Hit>) firesField.get(card);
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
        hits = (ArrayList<Hit>) firesField.get(randomCard);
        assertEquals(randomHits, hits);
        assertEquals(fires, hits);
    }
}