package controller.event.game;

import controller.event.EventType;

/**
 * This class contains all the events that can be triggered during the game.
 */
public class GameEvents {
    public static EventType<NoPayload> GAME_START = new EventType<>(NoPayload.class);
    public static EventType<NoPayload> GAME_END = new EventType<>(NoPayload.class);
    public static EventType<UseEngine> USE_ENGINE = new EventType<>(UseEngine.class);
    public static EventType<UseCannons> USE_CANNONS = new EventType<>(UseCannons.class);
    public static EventType<CrewLoss> REMOVE_CREW = new EventType<>(CrewLoss.class);
    public static EventType<ExchangeGoods> SWAP_GOODS = new EventType<>(ExchangeGoods.class);
    public static EventType<AcceptAbandonedShip> ACCEPT_ABANDONED_SHIP = new EventType<>(AcceptAbandonedShip.class);
}
