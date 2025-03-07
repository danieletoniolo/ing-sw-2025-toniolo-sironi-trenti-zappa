package Model.Good;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class GoodTest {
    Good good;

    @BeforeEach
    void setUp() {
        good = new Good(GoodType.BLUE);
        assertNotNull(good, "Variabile non dichiarata correttamente");
    }

    @Test
    void testGetColor() {
        assertTrue(Arrays.asList(GoodType.values()).contains(good.getColor()), "Il colore restituito non è valido");
    }

    @Test
    void testGetValue() {
        assertEquals(good.getColor().getValue(), good.getValue(), "Il valore del good non è corretto");
    }

    /*
    TEST GENERICO

    @ParameterizedTest
    @EnumSource(GoodType.class)
    void testGetColor(GoodType color) {
        good = new Good(color);
        assertNotNull(good, "Variabile non dichiarata correttamente");
        assertTrue(Arrays.asList(GoodType.values()).contains(good.getColor()), "Il colore restituito non è valido");
    }

    @ParameterizedTest
    @EnumSource(GoodType.class)
    void testGetValue(GoodType color) {
        good = new Good(color);
        assertNotNull(good, "Variabile non dichiarata correttamente");
        assertEquals(color.getValue(), good.getValue(), "Il valore del good non è corretto");
    }
    */
}