package Model.Cards.Hits;

import it.polimi.ingsw.model.cards.hits.Direction;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.cards.hits.HitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HitTest {
    Hit hit;
    Field typeField = Hit.class.getDeclaredField("type");
    Field directionField = Hit.class.getDeclaredField("direction");

    HitTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        hit = new Hit(HitType.HEAVYFIRE, Direction.NORTH);
        assertNotNull(hit);
        typeField.setAccessible(true);
        directionField.setAccessible(true);
    }

    @Test
    void getType() throws IllegalAccessException {
        assertEquals(HitType.HEAVYFIRE, typeField.get(hit));
    }

    @Test
    void getDirection() throws IllegalAccessException {
        assertEquals(Direction.NORTH, directionField.get(hit));
    }

    @Test
    void testGetTypeDirection() {
        assertEquals(0, Direction.NORTH.getValue());
        assertEquals(1, Direction.WEST.getValue());
        assertEquals(2, Direction.SOUTH.getValue());
        assertEquals(3, Direction.EAST.getValue());
    }

    private static final Random RANDOM = new Random();

    private HitType getRandomHitType() {
        HitType[] types = HitType.values();
        return types[RANDOM.nextInt(types.length)];
    }

    private Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[RANDOM.nextInt(directions.length)];
    }

    @RepeatedTest(10)
    void testRandomHitCreation() throws IllegalAccessException {
        HitType randomType = getRandomHitType();
        Direction randomDirection = getRandomDirection();

        Hit hit = new Hit(randomType, randomDirection);

        assertNotNull(hit);
        assertEquals(randomType, typeField.get(hit));
        assertEquals(randomDirection, directionField.get(hit));
    }

    @Test
    void testAllPossibleCombinations() throws IllegalAccessException {
        for (HitType type : HitType.values()) {
            for (Direction direction : Direction.values()) {
                Hit hit = new Hit(type, direction);
                assertNotNull(hit);
                assertEquals(type, typeField.get(hit));
                assertEquals(direction, directionField.get(hit));
            }
        }
    }

    @RepeatedTest(5)
    void hitWithNullValues() throws IllegalAccessException {
        Hit hit = new Hit();
        assertNull(typeField.get(hit));
        assertNull(directionField.get(hit));
    }
}