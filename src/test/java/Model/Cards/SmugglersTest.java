package Model.Cards;

import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class SmugglersTest {
    Smugglers card;
    List<Good> rewards;
    PlayerData player;

    @BeforeEach
    void setUp() {
        rewards = new ArrayList<>();
        rewards.add(new Good(GoodType.BLUE));
        rewards.add(new Good(GoodType.RED));
        card = new Smugglers(rewards, 1, 2, 3, 4);
        assertNotNull(card, "Card variable not initialized correctly");
    }

    @Test
    void getGoodsReward() {
        List<Good> check = card.getGoodsReward();
        assertEquals(rewards, check);
        assertEquals(2, check.size());
        assertEquals(GoodType.BLUE, check.get(0).getColor());
        assertEquals(GoodType.RED, check.get(1).getColor());
    }

    @Test
    void getGoodsLoss() {
        assertEquals(1, card.getGoodsLoss());
    }

    @Test
    void getCardLevel() {
        assertEquals(2, card.getCardLevel());
    }

    @Test
    void getCannonStrengthRequired() {
        assertEquals(3,card.getCannonStrengthRequired());
    }

    @Test
    void getFlightDays() {
        assertEquals(4,card.getFlightDays());
    }

    @Test
    void getCardType() {
        assertEquals(CardType.SMUGGLERS, card.getCardType());
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


    @RepeatedTest(3)
    void testRandomGoodsLoss() {
        Random random = new Random();
        int goodsLoss = random.nextInt(card.getGoodsLoss()) + 1;
        Smugglers smugglers = new Smugglers(Collections.emptyList(), goodsLoss, 1, 5, 3);
        assertEquals(goodsLoss, smugglers.getGoodsLoss());
    }
}