package Model.Cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MeteorSwarmTest {
    MeteorSwarm card;

    @BeforeEach
    void setUp() {
        List<Hit> meteor = List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH));
        card = new MeteorSwarm(2, 0, meteor);
        assertNotNull(card);
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2,card.getCardLevel());

        Random rand = new Random();
        int level = rand.nextInt(3) + 1;
        MeteorSwarm randomCard = new MeteorSwarm(level, 0, List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH)));
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(0,card.getID());

        Random rand = new Random();
        int id = rand.nextInt(3) + 1;
        MeteorSwarm randomCard = new MeteorSwarm(1, id, List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH)));
        assertEquals(id, randomCard.getID());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.METEORSWARM, card.getCardType());
    }

    @RepeatedTest(5)
    void getMeteors() {
        List<Hit> meteors = new ArrayList<>();
        meteors.add(new Hit(HitType.SMALLMETEOR, Direction.NORTH));
        meteors.add(new Hit(HitType.LARGEMETEOR, Direction.SOUTH));
        MeteorSwarm card = new MeteorSwarm(2, 3, meteors);
        assertEquals(meteors, card.getMeteors());
        assertEquals(2, card.getMeteors().size());
        assertEquals(HitType.SMALLMETEOR, card.getMeteors().get(0).getType());
        assertEquals(HitType.LARGEMETEOR, card.getMeteors().get(1).getType());
        assertEquals(Direction.NORTH, card.getMeteors().get(0).getDirection());
        assertEquals(Direction.SOUTH, card.getMeteors().get(1).getDirection());

    }
}