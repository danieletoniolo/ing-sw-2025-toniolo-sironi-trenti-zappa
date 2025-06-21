package it.polimi.ingsw.model.cards.hits;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    void testGetHitType() {
        assertEquals(0, HitType.SMALLMETEOR.getValue());
        assertEquals(1, HitType.LARGEMETEOR.getValue());
        assertEquals(2, HitType.LIGHTFIRE.getValue());
        assertEquals(3, HitType.HEAVYFIRE.getValue());
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

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void fromInt_correctDirectionForValidValues(int value) {
        Direction direction = Direction.fromInt(value);
        assertNotNull(direction);
        assertEquals(value, direction.getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 4, 10})
    void fromInt_forInvalidValues_Direction(int value) {
        assertThrows(IllegalArgumentException.class, () -> Direction.fromInt(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void fromInt_correctHitTypeForValidValues(int value) {
        HitType hittype = HitType.fromInt(value);
        assertNotNull(hittype);
        assertEquals(value, hittype.getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 4, 10})
    void fromInt_forInvalidValues_HitType(int value) {
        assertThrows(IllegalArgumentException.class, () -> HitType.fromInt(value));
    }
}