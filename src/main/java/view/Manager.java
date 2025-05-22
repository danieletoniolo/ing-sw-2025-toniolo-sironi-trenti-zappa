package view;

import event.lobby.CreateLobby;
import event.lobby.JoinLobby;
import event.lobby.LeaveLobby;
import event.lobby.RemoveLobby;

public interface Manager {
    void notifyCreateLobby(CreateLobby data);

    void notifyRemoveLobby(RemoveLobby data);

    void notifyJoinLobby(JoinLobby data);

    void notifyLeaveLobby(LeaveLobby data);



}