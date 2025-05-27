package it.polimi.ingsw.view;

import it.polimi.ingsw.event.game.serverToClient.*;
import it.polimi.ingsw.event.lobby.serverToClient.*;

public interface Manager {
    void notifyUserIDSet();

    void notifyNicknameSet();

    void notifyLobbies();

    void notifyCreatedLobby(LobbyCreated data);

    void notifyLobbyJoined(LobbyJoined data);

    void notifyLobbyLeft(LobbyLeft data);

    void notifyLobbyRemoved(LobbyRemoved data);

    void notifyCountDown();

    void notifyStartingGame();


    void notifyBestLookingShips(BestLookingShips data);

    void notifyCannonsUsed(CannonsUsed data);

    void notifyCanShield(CanProtect data);

    void notifyComponentDestroyed(ComponentDestroyed data);

    void notifyEnemyLost(EnemyDefeat data);

    void notifyEnemyWon(EnemyDefeat data);

    void notifyEnemyDrew(EnemyDefeat data);

    void notifyEnginesUsed(EnginesUsed data);
}