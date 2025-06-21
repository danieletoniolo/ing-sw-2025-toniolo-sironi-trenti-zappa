package it.polimi.ingsw.model.good;

import it.polimi.ingsw.model.cards.hits.HitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GoodTest {
    Good good;
    Field colorField = Good.class.getDeclaredField("color"); //Uno per ogni variabile -- nei "" metto nome variabile
    //Prendo la variabile della classe

    GoodTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        good = new Good(GoodType.BLUE);
        assertNotNull(good);
        colorField.setAccessible(true);
    }

    @Test
    void testGoodConstructor() throws IllegalAccessException {
        Good g1 = new Good();
        assertNotNull(g1);
        assertNull(colorField.get(g1)); //getColor
        colorField.set(g1, GoodType.BLUE); //setColor
    }

    @Test
    void testGetColor() throws IllegalAccessException {
        GoodType goodType = (GoodType) colorField.get(good);
        assertTrue(Arrays.asList(GoodType.values()).contains(goodType));
    }

    @Test
    void testGetValue() throws IllegalAccessException {
        GoodType goodType = (GoodType) colorField.get(good);
        assertEquals(goodType.getValue(), good.getValue());
    }

    @Test
    void testEquals() {
        Good otherGood = new Good(GoodType.BLUE);
        assertEquals(good, otherGood);

        Good otherGood1 = new Good(GoodType.RED);
        assertNotEquals(good, otherGood1);

        assertNotEquals(null, good);

        assertNotEquals(new Object(), good);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void fromInt_correctGoodTypeForValidValues(int value) {
        GoodType g = GoodType.fromInt(value);
        assertNotNull(g);
        assertEquals(value, g.getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 5, 10})
    void fromInt_forInvalidValues_GoodType(int value) {
        assertThrows(IllegalArgumentException.class, () -> GoodType.fromInt(value));
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