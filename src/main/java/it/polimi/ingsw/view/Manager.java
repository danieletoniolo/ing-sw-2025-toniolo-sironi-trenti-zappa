package it.polimi.ingsw.view;

import it.polimi.ingsw.event.game.serverToClient.StateChanged;
import it.polimi.ingsw.event.game.serverToClient.deck.*;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.*;
import it.polimi.ingsw.event.game.serverToClient.goods.*;
import it.polimi.ingsw.event.game.serverToClient.pickedTile.PickedTileFromSpaceship;
import it.polimi.ingsw.event.game.serverToClient.placedTile.*;
import it.polimi.ingsw.event.game.serverToClient.planets.PlanetSelected;
import it.polimi.ingsw.event.game.serverToClient.player.*;
import it.polimi.ingsw.event.game.serverToClient.rotatedTile.RotatedTile;
import it.polimi.ingsw.event.game.serverToClient.spaceship.*;
import it.polimi.ingsw.event.game.serverToClient.timer.TimerFlipped;
import it.polimi.ingsw.event.lobby.serverToClient.*;

public interface Manager {
    void notifyUserIDSet();

    void notifyNicknameSet();

    void notifyLobbies();

    void notifyCreatedLobby(LobbyCreated data);

    void notifyLobbyJoined(LobbyJoined data);

    void notifyLobbyLeft(LobbyLeft data);

    void notifyLobbyRemoved(LobbyRemoved data);

    void notifyReadyPlayer();

    void notifyStartingGame(StartingGame data);

    void notifyCountDown();

    // Deck
    void notifyDrawCard();

    void notifyPickedLeftDeck(PickedLeftDeck data);

    // Dice
    void notifyDiceRolled(DiceRolled data);

    // Energy used
    void notifyBatteriesUsed(BatteriesLoss data);

    void notifyCannonsUsed(CannonsUsed data);

    void notifyEnginesUsed(EnginesUsed data);

    void notifyShieldUsed(ShieldUsed data);

    // Goods
    void notifyGoodsSwapped(GoodsSwapped data);

    void notifyUpdateGoodsExchange(UpdateGoodsExchange data);

    // Picked tile
    void notifyPickedTileFromBoard();

    void notifyPickedTileFromSpaceShip(PickedTileFromSpaceship data);

    void notifyPickedHiddenTile(String nickname);

    // Placed tile
    void notifyPlacedTileToBoard(PlacedTileToBoard data);

    void notifyPlacedTileToReserve(PlacedTileToReserve data);

    void notifyPlacedTileToSpaceship(PlacedTileToSpaceship data);

    // Planets
    void notifyPlanetSelected(PlanetSelected data);

    //Player
    void notifyEnemyDefeat(EnemyDefeat data);

    void notifyMinPlayer(MinPlayer data);

    void notifyMoveMarker(MoveMarker data);

    void notifyPlayerGaveUp(PlayerGaveUp data);

    void notifyPlayerLost(PlayerLost data);

    void notifyPlaying(CurrentPlayer data);

    void notifyScore(Score data);

    void notifyUpdateCoins(UpdateCoins data);

    // Rotated tile
    void notifyRotatedTile(RotatedTile data);

    // Spaceship
    void notifyBestLookingShips(BestLookingShips data);

    void notifyCanProtect(CanProtect data);

    void notifyComponentDestroyed(ComponentDestroyed data);

    void notifyFragments(Fragments data);

    void notifyInvalidComponents(InvalidComponents data);

    void notifyNextHit(NextHit data);

    void notifyUpdateCrewMembers(UpdateCrewMembers data);

    void notifyLastTimerFlipped();
    // Timer
    void notifyTimer(TimerFlipped data);

    void notifyTimerFinished(TimerFlipped data);


    void notifyStateChange();
}