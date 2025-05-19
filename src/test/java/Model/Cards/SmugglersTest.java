package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SmugglersTest {
    Smugglers card;
    List<Good> rewards;
    Field cannonStrengthRequiredField = Smugglers.class.getDeclaredField("cannonStrengthRequired");
    Field flightDaysField = Smugglers.class.getDeclaredField("flightDays");
    Field goodsRewardField = Smugglers.class.getDeclaredField("goodsReward");
    Field goodsLossField = Smugglers.class.getDeclaredField("goodsLoss");
    Field IDField = Card.class.getDeclaredField("ID");
    Field levelField = Card.class.getDeclaredField("level");

    SmugglersTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        rewards = new ArrayList<>();
        rewards.add(new Good(GoodType.BLUE));
        rewards.add(new Good(GoodType.RED));
        card = new Smugglers(2, 0, rewards, 1, 2, 3);
        assertNotNull(card);
        cannonStrengthRequiredField.setAccessible(true);
        flightDaysField.setAccessible(true);
        goodsRewardField.setAccessible(true);
        goodsLossField.setAccessible(true);
        IDField.setAccessible(true);
        levelField.setAccessible(true);
    }

    @Test
    void testConstructor() throws IllegalAccessException {
        Smugglers c1 = new Smugglers();
        assertNotNull(c1);
        assertEquals(0, IDField.get(c1));
        assertEquals(0, levelField.get(c1));
        assertEquals(0, cannonStrengthRequiredField.get(c1));
        assertEquals(0, flightDaysField.get(c1));
        assertEquals(0, goodsLossField.get(c1));
        assertNull(goodsRewardField.get(c1));
        assertEquals(CardType.SMUGGLERS, c1.getCardType());
    }

    @Test
    void testRewardsNull() {
        assertThrows(NullPointerException.class, () -> new Smugglers(2, 3, null, 1, 2, 3));
    }

    @RepeatedTest(5)
    void getCardLevel() throws IllegalAccessException {
        assertEquals(2, levelField.get(card));
    }

    @RepeatedTest(5)
    void getCardID() throws IllegalAccessException {
        assertEquals(0, IDField.get(card));

        Random random = new Random();
        int id = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(1, id, rewards, 1, 2, 3);
        assertEquals(id, IDField.get(randomCard));
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SMUGGLERS, card.getCardType());
    }

    @RepeatedTest(5)
    void getCannonStrengthRequired() throws IllegalAccessException {
        assertEquals(2, cannonStrengthRequiredField.get(card));

        Random random = new Random();
        int cannonStrength = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(2, 0, rewards, 1, cannonStrength, 3);
        assertEquals(cannonStrength, cannonStrengthRequiredField.get(randomCard));
    }

    @RepeatedTest(5)
    void getFlightDays() throws IllegalAccessException {
        assertEquals(3, flightDaysField.get(card));

        Random random = new Random();
        int flightDays = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(2, 0, rewards, 1, 2, flightDays);
        assertEquals(flightDays, flightDaysField.get(randomCard));
    }

    @RepeatedTest(5)
    void getGoodsLoss() throws IllegalAccessException {
        assertEquals(1, goodsLossField.get(card));

        Random random = new Random();
        int goodsLoss = random.nextInt(3) + 1;
        Smugglers randomCard = new Smugglers(2, 0, rewards, goodsLoss, 2, 3);
        assertEquals(goodsLoss, goodsLossField.get(randomCard));
    }

    @RepeatedTest(5)
    void getGoodsReward() throws IllegalAccessException {
        List<Good> check = (List<Good>) goodsRewardField.get(card);
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
        assertEquals(checks, goodsRewardField.get(randomCard));
    }
}