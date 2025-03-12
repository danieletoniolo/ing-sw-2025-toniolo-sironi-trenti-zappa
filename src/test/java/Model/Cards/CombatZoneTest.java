package Model.Cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class CombatZoneTest {
    CombatZone card;
    List<Hit> fires;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        fires.add(new Hit(HitType.HEAVYFIRE, Direction.EAST));
        fires.add(new Hit(HitType.LARGEMETEOR, Direction.NORTH));
        assertFalse(fires.contains(null));
        card = new CombatZone(2, 3, fires, 2);
        assertNotNull(card, "Card variable not initialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());
    }

    @Test
    void getFlightDays() {
        assertEquals(2,card.getFlightDays());
    }

    @Test
    void getLost() {
        assertEquals(3,card.getLost());
    }

    @Test
    void getFires(){
        List<Hit> hits = card.getFires();
        assertEquals(fires, hits);
        assertEquals(2,hits.size());
        assertEquals(HitType.HEAVYFIRE,hits.get(0).getType());
        assertEquals(HitType.LARGEMETEOR,hits.get(1).getType());
        assertEquals(Direction.EAST,hits.get(0).getDirection());
        assertEquals(Direction.NORTH,hits.get(1).getDirection());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.COMBATZONE, card.getCardType());
    }

    @Test
    void apply() {

    }


    @ParameterizedTest
    @CsvSource({"7, 3", "10, 5", "2, 1"})
    void testRepeatedCombatZoneInitialization(int flightDays, int lost) {
        List<Hit> hits = Collections.emptyList();
        CombatZone zone = new CombatZone(flightDays, lost, hits, 1);

        assertEquals(flightDays, zone.getFlightDays());
        assertEquals(lost, zone.getLost());
    }

    @RepeatedTest(5)
    void testRandomizedCombatZone() {
        Random random = new Random();
        int flightDays = random.nextInt(card.getFlightDays()) + 1;
        int lost = random.nextInt(card.getLost()) + 1;
        List<Hit> hits = Collections.emptyList();
        CombatZone zone = new CombatZone(flightDays, lost, hits, 1);

        assertEquals(flightDays, zone.getFlightDays());
        assertEquals(lost, zone.getLost());
    }
}