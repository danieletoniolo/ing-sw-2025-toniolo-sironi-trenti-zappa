package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SmugglersTest {
    Smugglers card;
    List<Good> rewards;

    @BeforeEach
    void setUp() {
        rewards = new ArrayList<>();
        rewards.add(new Good(GoodType.BLUE));
        rewards.add(new Good(GoodType.RED));
        card = new Smugglers(2, 0, rewards, 1, 2, 3);
        assertNotNull(card);
    }

    @Test
    void testConstructor() {
        Smugglers c1 = new Smugglers();
        assertNotNull(c1);
        assertEquals(0, c1.getID());
        assertEquals(0, c1.getCardLevel());
        assertEquals(0, c1.getCannonStrengthRequired());
        assertEquals(0, c1.getFlightDays());
        assertEquals(0, c1.getGoodsLoss());
        assertNull(c1.getGoodsReward());
        assertEquals(CardType.SMUGGLERS, c1.getCardType());
    }

    @Test
    void testRewardsNull() {
        assertThrows(NullPointerException.class, () -> new Smugglers(2, 3, null, 1, 2, 3));
    }

    @RepeatedTest(5)
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());

        Random random = new Random();
        int level = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(level, 0, rewards, 1, 2, 3);
        assertEquals(level, randomCard.getCardLevel());
    }

    @RepeatedTest(5)
    void getCardID() {
        assertEquals(0, card.getID());

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(1, id, rewards, 1, 2, 3);
        assertEquals(id, randomCard.getID());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SMUGGLERS, card.getCardType());
    }

    @RepeatedTest(5)
    void getCannonStrengthRequired() {
        assertEquals(2,card.getCannonStrengthRequired());

        Random random = new Random();
        int cannonStrength = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(2, 0, rewards, 1, cannonStrength, 3);
        assertEquals(cannonStrength, randomCard.getCannonStrengthRequired());
    }

    @RepeatedTest(5)
    void getFlightDays() {
        assertEquals(3,card.getFlightDays());

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(2, 0, rewards, 1, 2, flightDays);
        assertEquals(flightDays, randomCard.getFlightDays());
    }

    @RepeatedTest(5)
    void getGoodsLoss() {
        assertEquals(1, card.getGoodsLoss());

        Random random = new Random();
        int goodsLoss = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(2, 0, rewards, goodsLoss, 2, 3);
        assertEquals(goodsLoss, randomCard.getGoodsLoss());
    }

    @RepeatedTest(5)
    void getGoodsReward() {
        List<Good> check = card.getGoodsReward();
        assertEquals(rewards, check);
        assertEquals(2, check.size());
        assertEquals(GoodType.BLUE, check.get(0).getColor());
        assertEquals(GoodType.RED, check.get(1).getColor());

        Random random = new Random();
        List<Good> rewards = new ArrayList<>();
        List<Good> checks = new ArrayList<>();
        GoodType[] goodTypes = GoodType.values();
        for (int i = 0; i < 2; i++) {
            rewards.add(new Good(goodTypes[random.nextInt(goodTypes.length)]));
            checks.add(rewards.get(i));
        }
        Smugglers randomCard = new Smugglers(2, 0, rewards, 1, 2, 3);
        assertEquals(checks, randomCard.getGoodsReward());
    }
}