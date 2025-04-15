package Model.Player;

import Model.SpaceShip.SpaceShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDataTest {
    PlayerData player;
    SpaceShip ship = null;
    private static final String Character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @ParameterizedTest
    @ValueSource(strings = {"123e4567-e89b-12d3-a456-426614174001", "123e4567-e89b-12d3-a456-426614174002", "123e4567-e89b-12d3-a456-426614174003", "123e4567-e89b-12d3-a456-426614174004"})
    void getUsername(UUID username) {
        player = new PlayerData(username.toString(), PlayerColor.BLUE, ship);
        assertEquals(username.toString(), player.getUsername());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123e4567-e89b-12d3-a456-426614174001", "123e4567-e89b-12d3-a456-426614174002", "123e4567-e89b-12d3-a456-426614174003", "123e4567-e89b-12d3-a456-426614174004"})
    void getUUID(UUID username) {
        player = new PlayerData(username.toString(), PlayerColor.BLUE, ship);
        assertEquals(player.getUUID(), username);
    }

    @ParameterizedTest
    @EnumSource(PlayerColor.class)
    void getColor(PlayerColor color) {
        player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", color, ship);
        assertEquals(color, player.getColor());
    }

    @Test
    void setStep() {
        player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        player.setStep(5);
        assertEquals(5, player.getStep());
    }

    @Test
    void getStep() {
        player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        player.setStep(5);
        assertEquals(5, player.getStep());
    }

    @Test
    void setPosition() {
        player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        player.setPosition(5);
        assertEquals(5, player.getPosition());
    }

    @Test
    void getPosition() {
        player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        player.setPosition(5);
        assertEquals(5, player.getPosition());
    }

    @Test
    void getCoins() {
    }

    @Test
    void getSpaceShip() {
    }

    @Test
    void isLeader() {
    }

    @Test
    void isDisconnected() {
    }

    @Test
    void hasGivenUp() {
    }

    @Test
    void addCoins() {
        player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        player.addCoins(5);
        assertEquals(5, player.getCoins());
    }

    @Test
    void setGaveUp() {
    }

    @Test
    void setDisconnected() {
    }

    @Test
    void testEquals() {
    }

    /*
    PlayerData player;
    SpaceShip ship;
    private static final String Character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @BeforeEach
    void setUp() {
        player = new PlayerData("username", PlayerColor.BLUE, ship);
    }

    @RepeatedTest(5)
    void getUsername() {
        assertEquals("username", player.getUsername());

        Random rand = new Random();
        int length = rand.nextInt(5, 20);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Character.charAt(rand.nextInt(Character.length())));
        }
        System.out.println(sb.toString());

        PlayerData p = new PlayerData(sb.toString(), PlayerColor.YELLOW, ship);
        assertEquals(sb.toString(), p.getUsername());
    }

    @RepeatedTest(5)
    void getColor() {
        assertEquals(PlayerColor.BLUE, player.getColor());

        Random rand = new Random();
        PlayerColor[] colors = PlayerColor.values();
        PlayerColor color = colors[rand.nextInt(colors.length)];
        PlayerData p = new PlayerData("username", color, ship);
        assertEquals(color, p.getColor());
    }

    //Test for getSteps and addSteps
    @RepeatedTest(5)
    void Steps() {
        assertEquals(0, player.getStep());

        Random rand = new Random();
        int steps = rand.nextInt(-10, 10);
        player = new PlayerData("username", PlayerColor.BLUE, ship);
        //player.addStep(steps);
        assertEquals(steps, player.getStep());
    }

    //Test for getCoins and addCoins
    @RepeatedTest(5)
    void Coins() {
        assertEquals(0, player.getCoins());

        Random rand = new Random();
        int coins = rand.nextInt(1, 10);
        player = new PlayerData("username", PlayerColor.BLUE, ship);
        player.addCoins(coins);
        assertEquals(coins, player.getCoins());
    }

    @RepeatedTest(5)
    void getNumberOfLaps() {
        //assertEquals(0, player.getNumberOfLaps(24));

        Random rand = new Random();
        int steps = rand.nextInt(0, 100);
        player = new PlayerData("username", PlayerColor.BLUE, ship);
        //player.addSteps(steps);
        System.out.println(steps);
        //assertEquals(steps % 24, player.getNumberOfLaps(24));
    }

    @Test
    void getSpaceShip() {
        assertEquals(ship, player.getSpaceShip());

        boolean[][] spots = new boolean[12][12];
        SpaceShip ship1 = new SpaceShip(Level.SECOND, spots);
        PlayerData p = new PlayerData("username", PlayerColor.BLUE, ship1);
        assertEquals(ship1, p.getSpaceShip());
    }

    //Test for isLeader and setLeader
    @RepeatedTest(5)
    void Leader() {
        assertFalse(player.isLeader());
        //player.setLeader(true);
        assertTrue(player.isLeader());

        PlayerData p1 = new PlayerData("username", PlayerColor.BLUE, ship);
        PlayerData p2 = new PlayerData("username1", PlayerColor.RED, ship);
        Random rand = new Random();
        int day1 = 0, day2 = 0;
        do{
            day1 = rand.nextInt(1, 10);
            day2 = rand.nextInt(1, 10);
        }while (day1 == day2);
        if (day1 > day2) {
            //p1.setLeader(true);
            assertTrue(p1.isLeader());
            assertFalse(p2.isLeader());
        } else {
            //p2.setLeader(true);
            assertTrue(p2.isLeader());
            assertFalse(p1.isLeader());
        }
    }

    //Test for isDisconnected and setDisconnected
    //TODO: Devo dire che se sono disconnesso sono cmq in lobby? Tutto per renderlo diverso da give up
    @RepeatedTest(5)
    void Disconnected() {
        assertFalse(player.isDisconnected());
        player.setDisconnected(true);
        assertTrue(player.isDisconnected());

        Random rand = new Random();
        boolean disconnected = rand.nextBoolean();
        PlayerData p = new PlayerData("username", PlayerColor.BLUE, ship);
        if (disconnected) {
            p.setDisconnected(true);
            assertTrue(p.isDisconnected());
        } else {
            p.setDisconnected(false);
            assertFalse(p.isDisconnected());
        }
    }

    //Test for hasGivenUp and setGaveUp
    @RepeatedTest(5)
    void GivenUp() {
        assertFalse(player.hasGivenUp());
        player.setGaveUp(true);
        assertTrue(player.hasGivenUp());

        Random rand = new Random();
        boolean gaveUp = rand.nextBoolean();
        PlayerData p = new PlayerData("username", PlayerColor.BLUE, ship);
        if (gaveUp) {
            p.setGaveUp(true);
            assertTrue(p.hasGivenUp());
        } else {
            p.setGaveUp(false);
            assertFalse(p.hasGivenUp());
        }
    }

    @Test
    void testEquals() {
        PlayerData p1 = new PlayerData("username", PlayerColor.BLUE, ship);
        PlayerData p2 = new PlayerData("username", PlayerColor.BLUE, ship);
        assertTrue(p1.equals(p2));

        p1 = new PlayerData("username", PlayerColor.BLUE, ship);
        p2 = new PlayerData("username1", PlayerColor.BLUE, ship);
        assertFalse(p1.equals(p2));

        p1 = new PlayerData("username", PlayerColor.BLUE, ship);
        p2 = new PlayerData("username", PlayerColor.RED, ship);
        assertFalse(p1.equals(p2));

        p1 = new PlayerData("username", PlayerColor.BLUE, ship);
        p2 = new PlayerData("username", PlayerColor.BLUE, new SpaceShip(Level.SECOND, new boolean[12][12]));
        assertFalse(p1.equals(p2));
    }
    */
}