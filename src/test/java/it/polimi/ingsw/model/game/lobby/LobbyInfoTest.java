package it.polimi.ingsw.model.game.lobby;

import it.polimi.ingsw.model.game.board.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LobbyInfoTest {
    Field playersReadyField = LobbyInfo.class.getDeclaredField("playersReady");
    Field numberOfPlayersEnteredField = LobbyInfo.class.getDeclaredField("numberOfPlayersEntered");

    LobbyInfo lobbyInfo;

    LobbyInfoTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() {
        lobbyInfo = new LobbyInfo("p1", 4, Level.SECOND);
    }

    @Test
    void getName() {
        assertEquals("p1's lobby", lobbyInfo.getName());
    }

    @Test
    void getFounderNickname() {
        assertEquals("p1", lobbyInfo.getFounderNickname());
    }

    @Test
    void getLevel() {
        assertEquals(Level.SECOND, lobbyInfo.getLevel());
    }

    @Test
    void getTotalPlayers() {
        assertEquals(4, lobbyInfo.getTotalPlayers());
    }

    @Test
    void getNumberOfPlayersEntered() {
        assertEquals(0, lobbyInfo.getNumberOfPlayersEntered());
    }

    @Test
    void setName() {
        lobbyInfo.setName("New Name");
        assertEquals("New Name", lobbyInfo.getName());
    }

    @Test
    void canGameStart(){
        assertFalse(lobbyInfo.canGameStart());
    }

    @Test
    void addPlayer_and_addPlayerReady() throws IllegalAccessException {
        numberOfPlayersEnteredField.setAccessible(true);

        UUID playerId = UUID.randomUUID();
        lobbyInfo.addPlayer();
        assertEquals(1, numberOfPlayersEnteredField.get(lobbyInfo));
        lobbyInfo.addPlayerReady(playerId);
        assertTrue(lobbyInfo.isPlayerReady(playerId));
    }

    @Test
    void addPlayerReady_withDuplicatePlayer() throws IllegalAccessException {
        playersReadyField.setAccessible(true);

        UUID playerId = UUID.randomUUID();
        lobbyInfo.addPlayerReady(playerId);
        lobbyInfo.addPlayerReady(playerId);
        assertEquals(1, ((List<UUID>) playersReadyField.get(lobbyInfo)).size());
    }

    @Test
    void addPlayerReady_whenLobbyIsFull() {
        for (int i = 0; i < lobbyInfo.getTotalPlayers(); i++) {
            lobbyInfo.addPlayer();
            lobbyInfo.addPlayerReady(UUID.randomUUID());
        }
        assertThrows(IllegalStateException.class, () -> lobbyInfo.addPlayer());
        assertThrows(IllegalStateException.class, () -> lobbyInfo.addPlayerReady(UUID.randomUUID()));
    }

    @Test
    void removePlayer_and_removePlayerReady() throws IllegalAccessException {
        numberOfPlayersEnteredField.setAccessible(true);

        UUID playerId = UUID.randomUUID();
        lobbyInfo.addPlayer();
        lobbyInfo.addPlayerReady(playerId);
        lobbyInfo.removePlayerReady(playerId);
        assertFalse(lobbyInfo.isPlayerReady(playerId));
        lobbyInfo.removePlayer();
        assertEquals(0, numberOfPlayersEnteredField.get(lobbyInfo));
    }

    @Test
    void removePlayerReady_withZeroPlayer() throws IllegalAccessException {
        playersReadyField.setAccessible(true);

        UUID playerId = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> lobbyInfo.removePlayerReady(playerId));
    }









    /*
    LobbyInfo lobbyInfo;
    private static final String Character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @BeforeEach
    void setUp() {
        lobbyInfo = new LobbyInfo("Lobby", 4);
    }

    @RepeatedTest(5)
    void getName() {
        assertEquals("Lobby", lobbyInfo.getName());

        Random rand = new Random();
        int length = rand.nextInt(5, 20);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Character.charAt(rand.nextInt(Character.length())));
        }
        System.out.println(sb.toString());

        lobbyInfo = new LobbyInfo(sb.toString(), 4);
        assertEquals(sb.toString(), lobbyInfo.getName());
    }

    @RepeatedTest(5)
    void getTotalPlayers() {
        assertEquals(4, lobbyInfo.getTotalPlayers());

        Random rand = new Random();
        int totalPlayers = rand.nextInt(1, 10);
        lobbyInfo = new LobbyInfo("Lobby", totalPlayers);
        assertEquals(totalPlayers, lobbyInfo.getTotalPlayers());
    }

    @RepeatedTest(5)
    void testPlayers() {
        assertEquals(0, lobbyInfo.getNumberOfPlayersEntered());

        Random rand = new Random();
        Level level = Level.SECOND;
        boolean[][] spots = new boolean[12][12];
        int numberOfPlayersEntered = rand.nextInt(1, 4);
        System.out.println(numberOfPlayersEntered);
        lobbyInfo = new LobbyInfo("Lobby", 4);
        PlayerColor[] colors = PlayerColor.values();

        for (int i = 0; i < numberOfPlayersEntered; i++) {
            SpaceShip ship = new SpaceShip(level, spots);

            int length = rand.nextInt(5, 20);
            StringBuilder sb = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                sb.append(Character.charAt(rand.nextInt(Character.length())));
            }
            System.out.println(sb.toString());

            PlayerData p = new PlayerData(sb.toString(), colors[i], ship);
            System.out.println(p.getUsername() + " " + p.getColor() + " " + p.getSpaceShip());

            //Test for addPlayer
            lobbyInfo.addPlayer(p);
        }
        //Test for getNumberOfPlayersEntered
        assertEquals(numberOfPlayersEntered, lobbyInfo.getNumberOfPlayersEntered());

        //Test for getPlayers
        ArrayList<PlayerData> players = lobbyInfo.getPlayers();
        for (int i = 0; i < numberOfPlayersEntered; i++) {
            assertEquals(players.get(i).getUsername(), lobbyInfo.getPlayers().get(i).getUsername());
            assertEquals(players.get(i).getColor(), lobbyInfo.getPlayers().get(i).getColor());
            assertEquals(players.get(i).getSpaceShip(), lobbyInfo.getPlayers().get(i).getSpaceShip());
        }

        //Test for removePlayer
        int index = rand.nextInt(0, numberOfPlayersEntered);
        System.out.println("Index: " + index);
        for(int i = 0; i < index; i++) {
            System.out.println(lobbyInfo.getPlayers().get(i).getUsername() + " " + lobbyInfo.getPlayers().get(i).getColor() + " " + lobbyInfo.getPlayers().get(i).getSpaceShip());
            lobbyInfo.removePlayer(lobbyInfo.getPlayers().get(i));
            i--;
            index--;
            numberOfPlayersEntered--;
        }
        assertEquals(numberOfPlayersEntered, lobbyInfo.getNumberOfPlayersEntered());
    }
     */
}