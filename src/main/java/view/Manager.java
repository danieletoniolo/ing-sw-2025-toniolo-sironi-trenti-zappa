package view;

import controller.event.game.AddCoins;
import controller.event.lobby.CreateLobby;
import controller.event.lobby.JoinLobby;
import controller.event.lobby.LeaveLobby;

public interface Manager {
    void notifyCreateLobby(CreateLobby data);

    void notifyJoinLobby(JoinLobby data);

    void notifyLeaveLobby(LeaveLobby data);

    void notifyAddCoins(AddCoins data);


}