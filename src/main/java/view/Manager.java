package view;

import it.polimi.ingsw.event.game.serverToClient.BestLookingShips;
import it.polimi.ingsw.event.lobby.serverToClient.*;

public interface Manager {
    void notifyNicknameSet();

    void notifyLobbies();

    void notifyCreatedLobby(LobbyCreated data);

    void notifyLobbyJoined(LobbyJoined data);

    void notifyLobbyLeft(LobbyLeft data);

    void notifyLobbyRemoved(LobbyRemoved data);

    void notifyBestLookingShips(BestLookingShips data);
}