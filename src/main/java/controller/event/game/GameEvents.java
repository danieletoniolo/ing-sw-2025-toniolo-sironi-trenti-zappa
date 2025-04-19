package controller.event.game;

import controller.event.EventType;

public class GameEvents {
    public static EventType<UseEngine> USE_ENGINE = new EventType<>();
    public static EventType<UseCannons> USE_CANNONS = new EventType<>();
    public static EventType<RemoveCrew> REMOVE_CREW = new EventType<>();
}
