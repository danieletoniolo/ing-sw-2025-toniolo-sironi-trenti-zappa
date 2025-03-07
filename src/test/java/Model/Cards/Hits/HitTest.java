package Model.Cards.Hits;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HitTest {
    Hit hit;

    @BeforeEach
    void setUp() {
        hit = new Hit(HitType.HEAVYFIRE, Direction.NORD);
        assertNotNull(hit, "Hit variable not initialized correctly");
    }

    @Test
    void getType() {
        assertEquals(HitType.HEAVYFIRE, hit.getType());
    }

    @Test
    void getDirection() {
        assertEquals(Direction.NORD, hit.getDirection());
    }
}