package Model.Cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Player.PlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class PiratesTest {
    Pirates card;
    List<Hit> fires;
    PlayerData player;

    @BeforeEach
    void setUp() {
        fires = new ArrayList<>();
        fires.add(new Hit(HitType.HEAVYFIRE, Direction.NORTH));
        fires.add(new Hit(HitType.LIGHTFIRE, Direction.SOUTH));
        assertFalse(fires.contains(null));
        card = new Pirates(fires, 4, 2, 1, 3);
        assertNotNull(card, "Card variable not inialized correctly");
    }

    @Test
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());
    }

    @Test
    void getFire() {
        List<Hit> hits = card.getFire();
        assertEquals(fires,hits);
        assertEquals(2, hits.size());
        assertEquals(HitType.HEAVYFIRE, hits.get(0).getType());
        assertEquals(HitType.LIGHTFIRE, hits.get(1).getType());
        assertEquals(Direction.NORTH, hits.get(0).getDirection());
        assertEquals(Direction.SOUTH, hits.get(1).getDirection());
    }

    @Test
    void getCannonStrengthRequired() {
        assertEquals(1,card.getCannonStrengthRequired());
    }

    @Test
    void getFlightDays() {
        assertEquals(3,card.getFlightDays());
    }

    @Test
    void getCredit() {
        assertEquals(4, card.getCredit());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.PIRATES, card.getCardType());
    }

    @Test
    void apply() {
    }

    @Test
    void isPlayed(){
        assertFalse(card.isPlayed());
        card.apply(player);
        //assertTrue(card.isPlayed());
    }
}