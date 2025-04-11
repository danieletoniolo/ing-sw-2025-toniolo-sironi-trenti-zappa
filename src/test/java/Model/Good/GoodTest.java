package Model.Good;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GoodTest {
    Good good;
    Random random = new Random();

    @BeforeEach
    void setUp() {
        good = new Good(GoodType.BLUE);
        assertNotNull(good);
    }

    @Test
    void testGoodConstructor() {
        Good g1 = new Good();
        assertNotNull(g1);
        assertNull(g1.getColor());
    }

    @Test
    void testGetColor() {
        assertTrue(Arrays.asList(GoodType.values()).contains(good.getColor()));
    }

    @Test
    void testGetValue() {
        assertEquals(good.getColor().getValue(), good.getValue());
    }

    @Test
    void testEquals() {
        assertTrue(good.equals(good));

        Good otherGood = new Good(GoodType.BLUE);
        assertTrue(good.equals(otherGood));

        Good otherGood1 = new Good(GoodType.RED);
        assertFalse(good.equals(otherGood1));

        assertFalse(good.equals(null));

        assertFalse(good.equals(new Object()));
    }

    /*
    @RepeatedTest(10)
    void testRandomizedGoodType() {
        GoodType[] values = GoodType.values();
        GoodType randomType = values[random.nextInt(values.length)];
        System.out.println(randomType);
        Good good = new Good(randomType);

        assertNotNull(good.getColor());
        assertEquals(randomType, good.getColor());
        assertEquals(randomType.getValue(), good.getValue());
    }

     */
}