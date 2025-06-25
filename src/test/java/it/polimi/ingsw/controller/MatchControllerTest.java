package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class MatchControllerTest {
    @Test
    void setUp_withValidNetworkTransceiver() {
        NetworkTransceiver transceiver = new NetworkTransceiver();

        assertDoesNotThrow(() -> MatchController.setUp(transceiver));
        assertNotNull(MatchController.getInstance());
    }

    @Test
    void getInstance_whenNotInitialized() throws Exception {
        java.lang.reflect.Field instanceField = MatchController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        assertNull(MatchController.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsNotInitialized() throws Exception {
        java.lang.reflect.Field instanceField = MatchController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        assertNull(MatchController.getInstance());
    }

    @Test
    void getNetworkTransceiverwhenLobbyInfoIsNull() {
        MatchController matchController = MatchController.getInstance();
        assertThrows(NullPointerException.class, () -> matchController.getNetworkTransceiver(null));
    }

    @Test
    void getNetworkTransceiver_whenLobbyInfoNotFound() {
        MatchController matchController = MatchController.getInstance();
        LobbyInfo nonExistentLobby = new LobbyInfo("NonExistent", 4, Level.SECOND);
        assertThrows(NullPointerException.class, () -> matchController.getNetworkTransceiver(nonExistentLobby));
    }
}