package controller.event.lobby;

import controller.event.EventType;

/**
 * This class contains all the events related to the lobby.
 */
public class LobbyEvents {
    public static  EventType<JoinLobbySuccessful> JOIN_LOBBY_SUCCESSFUL = new EventType<>(JoinLobbySuccessful.class);
    public static EventType<UserJoinedLobby> USER_JOINED_LOBBY = new EventType<>(UserJoinedLobby.class);
    public static EventType<UserLeftLobby> USER_LEFT_LOBBY = new EventType<>(UserLeftLobby.class);
}
