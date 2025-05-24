package view;

import event.game.serverToClient.BestLookingShips;
import event.lobby.serverToClient.*;

public interface Manager {
    void notifyNicknameSet();

    void notifyLobbies();

    void notifyCreatedLobby(LobbyCreated data);

    void notifyLobbyJoined(LobbyJoined data);

    void notifyLobbyLeft(LobbyLeft data);

    void notifyLobbyRemoved(LobbyRemoved data);

    void notifyBestLookingShips(BestLookingShips data);
}