/*
package controller;


import controller.event.Event;
import controller.event.EventType;
import controller.event.game.GameEvents;
import controller.event.game.NoPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    GameController gc;

    @BeforeEach
    void setUp() {
        gc = new GameController();
    }

    @Test
    void startGame_executesGameStartHandlersSuccessfully() {
        NoPayload info = new NoPayload();
        EventType<NoPayload> eventType = GameEvents.GAME_START;
        Consumer<NoPayload> handler = payload -> assertEquals(info, payload);
        //gc.addEventHandler(null, eventType, handler);

        gc.startGame();

        // Assuming handlers are executed in separate threads, add a small delay for execution
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Handler execution interrupted");
        }
    }

    @Test
    void startGame_whenNoHandlersExist() {
        assertDoesNotThrow(() -> gc.startGame());
    }
}

 */