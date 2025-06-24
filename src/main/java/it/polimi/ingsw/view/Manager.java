package it.polimi.ingsw.view;

import it.polimi.ingsw.event.game.serverToClient.deck.*;
import it.polimi.ingsw.event.game.serverToClient.dice.DiceRolled;
import it.polimi.ingsw.event.game.serverToClient.energyUsed.*;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingBatteriesPenalty;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingGiveUp;
import it.polimi.ingsw.event.game.serverToClient.forcingInternalState.ForcingPenalty;
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
    void notifyNicknameSet(NicknameSet data);

    void notifyConnectionLost();

    void notifyLobbies();

    void notifyCreatedLobby(LobbyCreated data);

    void notifyLobbyJoined(LobbyJoined data);

    void notifyLobbyLeft(LobbyLeft data);

    void notifyLobbyRemoved(LobbyRemoved data);

    void notifyReadyPlayer();

    void notifyCountDown();

    // Deck
    void notifyPickedLeftDeck(PickedLeftDeck data);

    // Dice
    void notifyDiceRolled(DiceRolled data);

    // Energy used
    void notifyBatteriesLoss(BatteriesLoss data);

    // Forcing internal state
    void notifyForcingBatteriesPenalty(ForcingBatteriesPenalty data);

    void notifyForcingGiveUp(ForcingGiveUp data);

    void notifyForcingPenalty(ForcingPenalty data);

    // Goods
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

    // Player
    void notifyCardPlayed(CardPlayed data);

    void notifyEnemyDefeat(EnemyDefeat data);

    void notifyMinPlayer(MinPlayer data);

    void notifyMoveMarker(MoveMarker data);

    void notifyPlayerGaveUp(PlayerGaveUp data);

    void notifyCurrentPlayer(CurrentPlayer data);

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

    void notifyHitComing(HitComing data);

    void notifySetCannonStrength(SetCannonStrength data);

    void notifySetEngineStrength(SetEngineStrength data);

    void notifyUpdateCrewMembers(UpdateCrewMembers data);

    // Timer
    void notifyTimer(TimerFlipped data, boolean firstSecond);

    void notifyTimerFinished(TimerFlipped data);

    void notifyLastTimerFlipped();

    void notifyStateChange();
}