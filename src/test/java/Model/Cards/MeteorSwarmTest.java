package Model.Cards;

import it.polimi.ingsw.model.cards.hits.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardType;
import it.polimi.ingsw.model.cards.MeteorSwarm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MeteorSwarmTest {
    MeteorSwarm card;
    Field meteorField = MeteorSwarm.class.getDeclaredField("meteors");
    Field levelField = Card.class.getDeclaredField("level");
    Field idField = Card.class.getDeclaredField("ID");

    MeteorSwarmTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        List<Hit> meteor = List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH));
        card = new MeteorSwarm(2, 0, meteor);
        assertNotNull(card);
        meteorField.setAccessible(true);
        levelField.setAccessible(true);
        idField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        MeteorSwarm c1 = new MeteorSwarm();
        assertNotNull(c1);
        assertEquals(0, idField.get(c1));
        assertEquals(0, levelField.get(c1));
        assertNull(meteorField.get(c1));
        assertEquals(CardType.METEORSWARM, c1.getCardType());
    }

    @Test
    void testMeteorsEmptyOrNull() {
        assertThrows(NullPointerException.class, () -> new MeteorSwarm(2, 3, null));
        assertThrows(NullPointerException.class, () -> new MeteorSwarm(2, 3, new ArrayList<>()));
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, levelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0, idField.get(card));

        Random rand = new Random();
        int id = rand.nextInt(3) + 1;
        MeteorSwarm randomCard = new MeteorSwarm(1, id, List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH)));
        assertEquals(id, idField.get(randomCard));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.METEORSWARM, card.getCardType());
    }

    @RepeatedTest(5)
    void getMeteors() throws IllegalAccessException {
        List<Hit> meteors = new ArrayList<>();
        meteors.add(new Hit(HitType.SMALLMETEOR, Direction.NORTH));
        meteors.add(new Hit(HitType.LARGEMETEOR, Direction.SOUTH));
        MeteorSwarm card = new MeteorSwarm(2, 3, meteors);
        List<Hit> result = (List<Hit>) meteorField.get(card);
        assertEquals(meteors, result);
        assertEquals(2, result.size());
        assertEquals(HitType.SMALLMETEOR, result.get(0).getType());
        assertEquals(HitType.LARGEMETEOR, result.get(1).getType());
        assertEquals(Direction.NORTH, result.get(0).getDirection());
        assertEquals(Direction.SOUTH, result.get(1).getDirection());

    }
}