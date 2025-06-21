package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class MatchControllerTest {

    @Test
    void setUp_withValidNetworkTransceiver() {
        NetworkTransceiver transceiver = new NetworkTransceiver();

        assertDoesNotThrow(() -> MatchController.setUp(transceiver));
        assertNotNull(MatchController.getInstance());
    }

    @Test
    void setUp_whenAlreadyInitialized() {
        NetworkTransceiver transceiver = new NetworkTransceiver();
        MatchController.setUp(transceiver);

        assertThrows(IllegalStateException.class, () -> MatchController.setUp(transceiver));
    }

    @Test
    void getInstance_whenNotInitialized() throws Exception {
        java.lang.reflect.Field instanceField = MatchController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        assertNull(MatchController.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsInitialized() {
        NetworkTransceiver transceiver = new NetworkTransceiver();
        MatchController.setUp(transceiver);

        assertNotNull(MatchController.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsNotInitialized() throws Exception {
        java.lang.reflect.Field instanceField = MatchController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        assertNull(MatchController.getInstance());
    }

    /*
    @Test
    void getNetworkTransceiver_withValidLobbyInfo() {
        LobbyInfo lobby = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver transceiver = new NetworkTransceiver();
        networkTransceivers.put(lobby, transceiver);
        MatchController matchController = MatchController.getInstance();

        assertEquals(transceiver, matchController.getNetworkTransceiver(lobby));
    }

    @Test
    void getNetworkTransceiver_withNullLobbyInfo() {
        assertThrows(NullPointerException.class, () -> matchController.getNetworkTransceiver(null));
    }

    @Test
    void getNetworkTransceiver_withNonExistentLobbyInfo() {
        LobbyInfo nonExistentLobby = new LobbyInfo("NonExistentLobby", 4, Level.SECOND);

        assertThrows(IllegalStateException.class, () -> matchController.getNetworkTransceiver(nonExistentLobby));
    }

     */
}