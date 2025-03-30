package Model.Game.Board;

import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.SpaceShip.ComponentType;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    Board board;
    boolean[][] vs = new boolean[12][12];
    SpaceShip ship = new SpaceShip(Level.SECOND, vs);
    Map<Integer, Component> tiles = null;
    Level level = Level.SECOND;
    PlayerData blue;
    PlayerData red;
    PlayerData green;
    PlayerData yellow;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                vs[i][j] = true;
            }
        }
        blue = new PlayerData(UUID.randomUUID().toString(), PlayerColor.BLUE, ship);
        red = new PlayerData(UUID.randomUUID().toString(), PlayerColor.RED, ship);
        green = new PlayerData(UUID.randomUUID().toString(), PlayerColor.GREEN, ship);
        yellow = new PlayerData(UUID.randomUUID().toString(), PlayerColor.YELLOW, ship);
        board = new Board(level, blue, red, green, yellow);
        assertNotNull(board);
    }

    @RepeatedTest(5)
    void getBoardLevel() throws JsonProcessingException {
        assertEquals(level, board.getBoardLevel());

        Random random = new Random();
        Level[] values = Level.values();
        Level randomLevel = values[random.nextInt(values.length)];
        board = new Board(randomLevel, blue, red, green, yellow);
        assertEquals(randomLevel, board.getBoardLevel());
    }

    @RepeatedTest(5)
    void getStepsForALapTest() throws JsonProcessingException {
        assertEquals(24, board.getStepsForALap());

        Random random = new Random();
        Level[] values = Level.values();
        Level randomLevel = values[random.nextInt(values.length)];
        board = new Board(randomLevel, blue, red, green, yellow);
        if(randomLevel.equals(Level.LEARNING)) {
            assertEquals(18, board.getStepsForALap());
        } else {
            assertEquals(24, board.getStepsForALap());
        }
    }

    @Test
    void getPlayer() throws JsonProcessingException {
        assertEquals(PlayerColor.RED, board.getPlayer(PlayerColor.RED).getColor());
        assertEquals(PlayerColor.BLUE, board.getPlayer(PlayerColor.BLUE).getColor());
        assertEquals(PlayerColor.GREEN, board.getPlayer(PlayerColor.GREEN).getColor());
        assertEquals(PlayerColor.YELLOW, board.getPlayer(PlayerColor.YELLOW).getColor());

        Board b1 = new Board(level, blue, red, green, null);
        assertNull(b1.getPlayer(PlayerColor.YELLOW));
    }

    @RepeatedTest(5)
    void getDeck() throws JsonProcessingException {
        assertThrows((NullPointerException.class), () -> {board.getDeck(0, null);});

        Board b1 = new Board(level, blue, red, green, yellow);

        for(int i = 0; i < 4; i++) {
            SpaceShip ship = new SpaceShip(Level.SECOND, vs);
            blue = new PlayerData(UUID.randomUUID().toString(), PlayerColor.BLUE, ship);
            Random random = new Random();
            if(i == 3) {  //TODO: last deck should be not pickable
                System.out.println("-\n");
                assertThrows((NullPointerException.class), () -> {b1.getDeck(3, null);});
            } else {
                boolean flag1 = random.nextBoolean();
                System.out.println(flag1 + "\n");
                if(flag1) {
                    //TODO: getDeck() should throw an exception if the player has only one component
                    int j = i;
                    assertThrows((IllegalStateException.class), () -> {b1.getDeck(j, blue);});
                } else {
                    Storage s1 = new Storage();
                    Storage s2 = new Storage();
                    ship.placeComponent(s1, 6, 7);
                    ship.placeComponent(s2, 6, 8);

                    assertNotNull(b1.getDeck(i, blue));
                    assertEquals(3, b1.getDeck(i, blue).getCards().size());
                }
            }
        }
    }

    @RepeatedTest(5)
    void getTile(){
        Random random = new Random();
        Component c = null;
        for(int i = 0; i < 5; i++) {
            int randomIndex = random.nextInt(0, 156);
            System.out.println(randomIndex);
            c = board.getTile(randomIndex);

            if(randomIndex >= 0 && randomIndex < 17) {
                assertEquals(ComponentType.BATTERY, c.getComponentType());
            } else if (randomIndex >= 16 && randomIndex < 32){
                assertEquals(ComponentType.STORAGE, c.getComponentType());
            } else if ((randomIndex >= 32 && randomIndex < 52) || randomIndex == 60){
                assertEquals(ComponentType.CABIN, c.getComponentType());
            } else if (randomIndex >= 52 && randomIndex < 60){
                assertEquals(ComponentType.CONNECTORS, c.getComponentType());
            } else if (randomIndex >= 61 && randomIndex < 70){
                assertEquals(ComponentType.STORAGE, c.getComponentType());
            } else if (randomIndex >= 70 && randomIndex < 91){
                assertEquals(ComponentType.SINGLE_ENGINE, c.getComponentType());
            } else if (randomIndex >= 91 && randomIndex < 100){
                assertEquals(ComponentType.DOUBLE_ENGINE, c.getComponentType());
            } else if (randomIndex >= 100 && randomIndex < 125){
                assertEquals(ComponentType.SINGLE_CANNON, c.getComponentType());
            } else if (randomIndex >= 125 && randomIndex < 136){
                assertEquals(ComponentType.DOUBLE_CANNON, c.getComponentType());
            } else if (randomIndex >= 136 && randomIndex < 142){
                assertEquals(ComponentType.BROWN_LIFE_SUPPORT, c.getComponentType());
            } else if (randomIndex >= 142 && randomIndex < 148){
                assertEquals(ComponentType.PURPLE_LIFE_SUPPORT, c.getComponentType());
            } else {
                assertEquals(ComponentType.SHIELD, c.getComponentType());
            }
            System.out.println(c.getComponentType() + "\n");
        }
    }

    @RepeatedTest(5)
    void stateTransition() throws JsonProcessingException {
        //TODO: implementare il metodo
    }

    @RepeatedTest(5)
    void drawCard() throws JsonProcessingException {
        Random random = new Random();
        int randomIndex = random.nextInt(1, 13);
        System.out.println(randomIndex);
        assertEquals(12, board.getShuffledDeck().size());
        for(int i = 0; i < randomIndex + 1; i++) {
            if(board.getShuffledDeck().isEmpty()) {
                assertThrows((IllegalStateException.class), () -> {board.drawCard();});
            } else {
                board.drawCard();
                assertEquals(12 - i - 1, board.getShuffledDeck().size());
            }
        }
    }
}