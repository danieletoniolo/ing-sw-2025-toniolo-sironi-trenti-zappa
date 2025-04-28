package Model.Game.Lobby;

import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LobbyInfoTest {
    LobbyInfo lobbyInfo;

    @BeforeEach
    void setUp() {
        lobbyInfo = new LobbyInfo("123e4567-e89b-12d3-a456-426616578345", 4);
    }

    @RepeatedTest(5)
    void addPlayer_validPlayer() {
        PlayerData player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, new boolean[12][12]));
        lobbyInfo.addPlayer(player);
        assertTrue(lobbyInfo.getPlayers().contains(player));
        assertEquals(1, lobbyInfo.getNumberOfPlayersEntered());
    }

    //TODO: Implementare
    @RepeatedTest(5)
    void addPlayer_duplicatePlayer() {
        PlayerData player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, new boolean[12][12]));
        lobbyInfo.addPlayer(player);
        assertThrows(IllegalArgumentException.class, () -> lobbyInfo.addPlayer(player));
    }

    @RepeatedTest(5)
    void removePlayer_validPlayer() {
        PlayerData player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, new boolean[12][12]));
        lobbyInfo.addPlayer(player);
        lobbyInfo.removePlayer(player);
        assertFalse(lobbyInfo.getPlayers().contains(player));
        assertEquals(0, lobbyInfo.getNumberOfPlayersEntered());
    }

    @RepeatedTest(5)
    void canGameStart_returnsTrue() {
        for (int i = 0; i < lobbyInfo.getTotalPlayers(); i++) {
            PlayerData player = new PlayerData(UUID.randomUUID().toString(), PlayerColor.values()[i % PlayerColor.values().length], new SpaceShip(Level.SECOND, new boolean[12][12]));
            lobbyInfo.addPlayer(player);
        }
        assertTrue(lobbyInfo.canGameStart());
    }

    @RepeatedTest(5)
    void canGameStart_returnsFalse() {
        for (int i = 0; i < lobbyInfo.getTotalPlayers() - 1; i++) {
            PlayerData player = new PlayerData(UUID.randomUUID().toString(), PlayerColor.values()[i % PlayerColor.values().length], new SpaceShip(Level.SECOND, new boolean[12][12]));
            lobbyInfo.addPlayer(player);
        }
        assertFalse(lobbyInfo.canGameStart());
    }

    @RepeatedTest(5)
    void canGameStart_returnsFalseWhenNoPlayersHaveJoined() {
        assertFalse(lobbyInfo.canGameStart());
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