package controller.event.game;

import controller.event.EventType;

/**
 * This class contains all the events that can be triggered during the game.
 */
public class GameEvents {
    public static EventType<NoPayload> GAME_START = new EventType<>();
    public static EventType<NoPayload> GAME_END = new EventType<>();
    public static EventType<UseEngine> USE_ENGINE = new EventType<>();
    public static EventType<UseCannons> USE_CANNONS = new EventType<>();
    public static EventType<RemoveCrew> REMOVE_CREW = new EventType<>();
    public static EventType<SwapGoods> SWAP_GOODS = new EventType<>();
}
