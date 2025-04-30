package Model.Cards.Hits;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HitTest {
    Hit hit;

    @BeforeEach
    void setUp() {
        hit = new Hit(HitType.HEAVYFIRE, Direction.NORTH);
        assertNotNull(hit);
    }

    @Test
    void getType() {
        assertEquals(HitType.HEAVYFIRE, hit.getType());
    }

    @Test
    void getDirection() {
        assertEquals(Direction.NORTH, hit.getDirection());
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
    void testRandomHitCreation() {
        HitType randomType = getRandomHitType();
        Direction randomDirection = getRandomDirection();

        Hit hit = new Hit(randomType, randomDirection);

        assertNotNull(hit);
        assertEquals(randomType, hit.getType());
        assertEquals(randomDirection, hit.getDirection());
    }

    @Test
    void testAllPossibleCombinations() {
        for (HitType type : HitType.values()) {
            for (Direction direction : Direction.values()) {
                Hit hit = new Hit(type, direction);
                assertNotNull(hit);
                assertEquals(type, hit.getType());
                assertEquals(direction, hit.getDirection());
            }
        }
    }

    @RepeatedTest(5)
    void hitWithNullValues() {
        Hit hit = new Hit();
        assertNull(hit.getType());
        assertNull(hit.getDirection());
    }
}