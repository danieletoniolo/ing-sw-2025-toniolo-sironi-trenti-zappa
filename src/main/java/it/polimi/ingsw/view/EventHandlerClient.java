package it.polimi.ingsw.view;


import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.game.serverToClient.*;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.view.miniModel.components.*;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.countDown.CountDown;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import org.javatuples.Triplet;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class EventHandlerClient {
    NetworkTransceiver transceiver;
    Manager manager;

    public EventHandlerClient(NetworkTransceiver transceiver, Manager manager) {
        this.transceiver = transceiver;
        this.manager = manager;


        /*
         * Set the userID of the client
         */
        CastEventReceiver<UserIDSet> userIDSetReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UserIDSet> userIDSetListener = data -> {
            MiniModel mm = MiniModel.getInstance();
            mm.setUserID(data.userID());

            manager.notifyUserIDSet();
        };
        userIDSetReceiver.registerListener(userIDSetListener);

        /*
         * Set the nickname of the player
         */
        CastEventReceiver<NicknameSet> nicknameSetReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<NicknameSet> nicknameSetListener = data -> {
            MiniModel.getInstance().setNickname(data.nickname());

            manager.notifyNicknameSet();
        };
        nicknameSetReceiver.registerListener(nicknameSetListener);

        // Lobby events
        /*
         * Set all the lobbies
         */
        CastEventReceiver<Lobbies> lobbiesReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Lobbies> lobbiesListener = data -> {
            for (int i = 0; i < data.lobbiesNames().size(); i++) {
                MiniModel.getInstance().getLobbiesView().add(new LobbyView(data.lobbiesNames().get(i), data.lobbiesPlayers().get(i).getValue0(),
                        data.lobbiesPlayers().get(i).getValue1(), LevelView.fromValue(data.lobbiesLevels().get(i))));
            }

            manager.notifyLobbies();
        };
        lobbiesReceiver.registerListener(lobbiesListener);

        /*
         * Create a new lobby, if it is created by this client it is set as tha main current lobby
         */
        CastEventReceiver<LobbyCreated> lobbyCreatedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyCreated> lobbyCreatedListener = data -> {
            LobbyView lobby = new LobbyView(data.lobbyID(), 1, data.maxPlayers(), LevelView.fromValue(data.level()));
            MiniModel.getInstance().getLobbiesView().add(lobby);
            if (data.nickname().equals(MiniModel.getInstance().getNickname())) {
                MiniModel.getInstance().setCurrentLobby(lobby);
            }

            manager.notifyCreatedLobby(data);
        };
        lobbyCreatedReceiver.registerListener(lobbyCreatedListener);

        /*
         * Add a new player inside a specific lobby
         */
        CastEventReceiver<LobbyJoined> lobbyJoinedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyJoined> lobbyJoinedListener = data -> {
            MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> {
                        lobby.addPlayer(data.nickname());
                    });

            manager.notifyLobbyJoined(data);
        };
        lobbyJoinedReceiver.registerListener(lobbyJoinedListener);

        /*
         * Remove a player from a specific lobby
         */
        CastEventReceiver<LobbyLeft> lobbyLeftReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyLeft> lobbyLeftListener = data -> {
            MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> {
                        lobby.removePlayer(data.nickname());
                    });

            manager.notifyLobbyLeft(data);
        };
        lobbyLeftReceiver.registerListener(lobbyLeftListener);

        /*
         * Remove a lobby
         */
        CastEventReceiver<LobbyRemoved> lobbyRemovedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyRemoved> lobbyRemovedListener = data -> {
            MiniModel.getInstance().getLobbiesView().stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> {
                        MiniModel.getInstance().getLobbiesView().remove(lobby);
                    });

            manager.notifyLobbyRemoved(data);
        };
        lobbyRemovedReceiver.registerListener(lobbyRemovedListener);

        /*
         * Start a countdown to announce that the game is starting
         */
        CastEventReceiver<StartingGame> startingGameReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<StartingGame> startingGameListener = data -> {
            new Thread(() -> {
                LocalDateTime serverTime = LocalDateTime.parse(data.startingTime());
                LocalDateTime clientTime = LocalDateTime.now();
                MiniModel.getInstance().setCountDown(new CountDown());
                int time = data.timerDuration() - Math.max(0, (int)Duration.between(serverTime, clientTime).toSeconds());
                while (time >= 0) {
                    try {
                        MiniModel.getInstance().getCountDown().setSecondsRemaining(time);
                        manager.notifyCountDown();
                        Thread.sleep(1000);
                        time--;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                MiniModel.getInstance().setCountDown(null);
                manager.notifyStartingGame();
            }).start();
        };
        startingGameReceiver.registerListener(startingGameListener);

        // Game events
        /*
         * Notify the best looking ships
         */
        CastEventReceiver<BestLookingShips> bestLookingShipsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<BestLookingShips> bestLookingShipsListener = data -> {

            manager.notifyBestLookingShips(data);
        };
        bestLookingShipsReceiver.registerListener(bestLookingShipsListener);

        /*
         * Remove a battery from the tiles in the batteriesIDs list
         */
        CastEventReceiver<CannonsUsed> cannonsUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<CannonsUsed> cannonsUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Integer id : data.batteriesIDs()) {
                    ((BatteryView) player.getShip().getMapBatteries().get(id)).reduceNumberOfButteries();
                }
            }
            manager.notifyCannonsUsed(data);
        };
        cannonsUsedReceiver.registerListener(cannonsUsedListener);

        CastEventReceiver<CanProtect> canProtectReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<CanProtect> canProtectListener = data -> {
            manager.notifyCanShield(data);
        };
        canProtectReceiver.registerListener(canProtectListener);

        CastEventReceiver<CardPlayed> cardPlayedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<CardPlayed> cardPlayedListener = data -> {
            //TODO
        };
        cardPlayedReceiver.registerListener(cardPlayedListener);


        /*
         * Move the tiles from the board to the discard pile
         */
        CastEventReceiver<ComponentDestroyed> componentDestroyedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<ComponentDestroyed> componentDestroyedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Pair<Integer, Integer> tile : data.destroyedComponents()) {
                    ComponentView tmp = player.getShip().removeComponent(tile.getValue0(), tile.getValue1());
                    player.getShip().getDiscardReservedPile().addDiscardReserved(tmp);
                }
            }

            manager.notifyComponentDestroyed(data);
        };
        componentDestroyedReceiver.registerListener(componentDestroyedListener);

        CastEventReceiver<EnemyDefeat> enemyDefeatReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<EnemyDefeat> enemyDefeatListener = data -> {
            if (data.enemyDefeat() == null) {
                manager.notifyEnemyDrew(data);
            }
            else if (data.enemyDefeat()) {
                manager.notifyEnemyLost(data);
            }
            else {
                manager.notifyEnemyLost(data);
            }
        };
        enemyDefeatReceiver.registerListener(enemyDefeatListener);

        /*
         * Remove a battery from the tiles in the batteriesIDs list
         */
        CastEventReceiver<EnginesUsed> enginesUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<EnginesUsed> enginesUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Integer id : data.batteriesIDs()) {
                    ((BatteryView) player.getShip().getMapBatteries().get(id)).reduceNumberOfButteries();
                }
            }
            manager.notifyEnginesUsed(data);
        };
        enginesUsedReceiver.registerListener(enginesUsedListener);

        /*
         * Set the fragments of the ship
         */
        CastEventReceiver<Fragments> fragmentsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Fragments> fragmentsListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                player.getShip().setFragments(data.fragments());
            }
        };
        fragmentsReceiver.registerListener(fragmentsListener);

        CastEventReceiver<GoodsSwapped> goodsSwappedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<GoodsSwapped> goodsSwappedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

        };
        goodsSwappedReceiver.registerListener(goodsSwappedListener);

        CastEventReceiver<MinPlayer> minPlayerReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<MinPlayer> minPlayerListener = data -> {

        };
        minPlayerReceiver.registerListener(minPlayerListener);

        /*
         * Move the marker of the player on the board
         */
        CastEventReceiver<MoveMarker> moveMarkerReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<MoveMarker> moveMarkerListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            MiniModel.getInstance().getBoardView().movePlayer(player.getMarkerView(), data.steps());
        };
        moveMarkerReceiver.registerListener(moveMarkerListener);

        CastEventReceiver<NextHit> nextHitReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<NextHit> nextHitListener = data -> {

        };
        nextHitReceiver.registerListener(nextHitListener);

        /*
         * Create a new BatteryView and set it in the player's hand
         */
        CastEventReceiver<PickedBatteryFromBoard> pickedBatteryFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedBatteryFromBoard> pickedBatteryFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            BatteryView battery = new BatteryView(data.tileID(), convertConnectors(data.connectors()), data.energyNumber());
            player.setHand(battery);

            //TODO manager.notify
        };
        pickedBatteryFromBoardReceiver.registerListener(pickedBatteryFromBoardListener);


        /*
         * Create a new CabinView and set it in the player's hand
         */
        CastEventReceiver<PickedCabinFromBoard> pickedCabinFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedCabinFromBoard> pickedCabinFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            CabinView cabin = new CabinView(data.tileID(), convertConnectors(data.connectors()));
            player.setHand(cabin);

            //TODO: notify
        };
        pickedCabinFromBoardReceiver.registerListener(pickedCabinFromBoardListener);

        /*
         * Create a new CannonView and set it in the player's hand
         */
        CastEventReceiver<PickedCannonFromBoard> pickedCannonFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedCannonFromBoard> pickedCannonFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            CannonView cannon = new CannonView(data.tileID(), convertConnectors(data.connectors()), data.cannonStrength(), 0);
            player.setHand(cannon);

            //TODO: notify
        };
        pickedCannonFromBoardReceiver.registerListener(pickedCannonFromBoardListener);

        /*
         * Create a new ConnectorsView and set it in the player's hand
         */
        CastEventReceiver<PickedConnectorsFromBoard> pickedConnectorsFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedConnectorsFromBoard> pickedConnectorsFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ConnectorsView pipes = new ConnectorsView(data.tileID(), convertConnectors(data.connectors()));
            player.setHand(pipes);

            //TODO: notify
        };
        pickedConnectorsFromBoardReceiver.registerListener(pickedConnectorsFromBoardListener);


        /*
         * Create a new EngineView and set it in the player's hand
         */
        CastEventReceiver<PickedEngineFromBoard> pickedEngineFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedEngineFromBoard> pickedEngineFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            EngineView engine = new EngineView(data.tileID(), convertConnectors(data.connectors()), data.cannonStrength(), 0);
            player.setHand(engine);

            //TODO notify
        };
        pickedEngineFromBoardReceiver.registerListener(pickedEngineFromBoardListener);

        /*
         * Set the viewable status of the deck. If deck == false it is not viewable in the building screen
         */
        CastEventReceiver<PickedLeftDeck> pickedLeftDeckReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedLeftDeck> pickedLeftDeckListener = data -> {
            MiniModel.getInstance().getDeckViews().getValue1()[data.deckIndex()] = data.usage() == 1;

            //TODO notify
        };
        pickedLeftDeckReceiver.registerListener(pickedLeftDeckListener);

        /*
         * Create a new LifeSupportView and set it in the player's hand
         */
        CastEventReceiver<PickedLifeSupportFromBoard> pickedLifeSupportFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedLifeSupportFromBoard> pickedLifeSupportFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ComponentView lifeSupport;
            if (data.type() == 1) {
                lifeSupport = new LifeSupportBrownView(data.tileID(), convertConnectors(data.connectors()));
            }
            else {
                lifeSupport = new LifeSupportPurpleView(data.tileID(), convertConnectors(data.connectors()));
            }
            player.setHand(lifeSupport);

            //TODO: notify
        };
        pickedLifeSupportFromBoardReceiver.registerListener(pickedLifeSupportFromBoardListener);

        /*
         * Create a new ShieldView and set it in the player's hand
         */
        CastEventReceiver<PickedShieldFromBoard> pickedShieldFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedShieldFromBoard> pickedShieldFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ShieldView shield = new ShieldView(data.tileID(), convertConnectors(data.connectors()), null);
            player.setHand(shield);

            //TODO: notify
        };
        pickedShieldFromBoardReceiver.registerListener(pickedShieldFromBoardListener);

        /*
         * Create a new StorageView and set it in the player's hand
         */
        CastEventReceiver<PickedStorageFromBoard> pickedStorageFromBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedStorageFromBoard> pickedStorageFromBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            StorageView storage = new StorageView(data.tileID(), convertConnectors(data.connectors()), false, data.goodsCapacity());
            player.setHand(storage);

            //TODO: notify
        };
        pickedStorageFromBoardReceiver.registerListener(pickedStorageFromBoardListener);

        /*
         * Remove a tile from the reserved list and add it into the hand
         */
        CastEventReceiver<PickedTileFromReserve> pickedTileFromReserveReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedTileFromReserve> pickedTileFromReserveListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ComponentView tile = player.getShip().getDiscardReservedPile().removeDiscardReserved(data.tileID());
            player.setHand(tile);

            //TODO: notify
        };
        pickedTileFromReserveReceiver.registerListener(pickedTileFromReserveListener);

        /**
         * Remove the last tile on the ship and add it into the hand
         */
        CastEventReceiver<PickedTileFromSpaceship> pickedTileFromSpaceshipReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PickedTileFromSpaceship> pickedTileFromSpaceshipListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.setHand(player.getShip().removeLast());

            //TODO notify
        };
        pickedTileFromSpaceshipReceiver.registerListener(pickedTileFromSpaceshipListener);

        /*
         * Remove the tile from the player's hand and add it to the viewable components
         */
        CastEventReceiver<PlacedTileToBoard> placedTileToBoardReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlacedTileToBoard> placedTileToBoardListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            MiniModel.getInstance().getViewableComponents().add(player.getHand());
            player.setHand(null);

            // TODO: notify
        };
        placedTileToBoardReceiver.registerListener(placedTileToBoardListener);

        /*
         * Remove tile from the player's hand and add it to the reserved pile
         */
        CastEventReceiver<PlacedTileToReserve> placedTileToReserveReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlacedTileToReserve> placedTileToReserveListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.getShip().getDiscardReservedPile().addDiscardReserved(player.getHand());
            player.setHand(null);

            //TODO: notify
        };
        placedTileToReserveReceiver.registerListener(placedTileToReserveListener);

        /*
         * Remove tile from the player's hand and add it to the spaceship
         */
        CastEventReceiver<PlacedTileToSpaceship> placedTileToSpaceshipReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlacedTileToSpaceship> placedTileToSpaceshipListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.getShip().placeComponent(player.getHand(), data.row(), data.column());
            player.setHand(null);

            //TODO notify
        };
        placedTileToSpaceshipReceiver.registerListener(placedTileToSpaceshipListener);

        /*
         * Notify the player has given up
         */
        CastEventReceiver<PlayerGaveUp> playerGaveUpReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlayerGaveUp> playerGaveUpListener = data -> {
            //TODO notify
        };
        playerGaveUpReceiver.registerListener(playerGaveUpListener);

        CastEventReceiver<PlayerLost> playerLostReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<PlayerLost> playerLostListener = data -> {
            //TODO
        };
        playerLostReceiver.registerListener(playerLostListener);

        /*
         * Notify who is the player who is playing
         */
        CastEventReceiver<Playing> playingReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Playing> playingListener = data -> {
            // TODO: notify
        };
        playingReceiver.registerListener(playingListener);

        CastEventReceiver<Score> scoreReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Score> scoreListener = data -> {
            //TODO
        };
        scoreReceiver.registerListener(scoreListener);

        /*
         * Reduce the number of battery
         */
        CastEventReceiver<ShieldUsed> shieldUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<ShieldUsed> shieldUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            ((BatteryView) player.getShip().getMapBatteries().get(data.batteryID())).reduceNumberOfButteries();
        };
        shieldUsedReceiver.registerListener(shieldUsedListener);

        CastEventReceiver<StateChanged> stateChangedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<StateChanged> stateChangedListener = data -> {
            // TODO: get the ID of the card
        };
        stateChangedReceiver.registerListener(stateChangedListener);

        /*
         * Notify that the timer is finish
         */
        CastEventReceiver<TimerFinish> timerFinishReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<TimerFinish> timerFinishListener = data -> {
            // TODO: notify -> cambiare con fase di building finita
        };
        timerFinishReceiver.registerListener(timerFinishListener);


        /*
         * Start the timer for the building phase
         */
        CastEventReceiver<TimerFlipped> timerFlippedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<TimerFlipped> timerFlippedListener = data -> {
            new Thread(() -> {
                LocalDateTime serverTime = LocalDateTime.parse(data.startingTime());
                LocalDateTime clientTime = LocalDateTime.now();
                MiniModel.getInstance().setTimerView(new TimerView(3));
                int time = (int)(data.timerDuration() - Math.max(0, Duration.between(serverTime, clientTime).toSeconds()));
                while (time >= 0) {
                    try {
                        MiniModel.getInstance().getTimerView().setSecondsRemaining(time);
                        // TODO notify
                        Thread.sleep(1000);
                        time--;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                MiniModel.getInstance().setTimerView(null);
            }).start();
        };
        timerFlippedReceiver.registerListener(timerFlippedListener);

        /*
         * Set the number if coins to the player
         */
        CastEventReceiver<UpdateCoins> updateCoinsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UpdateCoins> updateCoinsListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            player.setCoins(data.coins());

            //TODO notify
        };
        updateCoinsReceiver.registerListener(updateCoinsListener);

        CastEventReceiver<UpdateCrewMembers> updateCrewMembersReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UpdateCrewMembers> updateCrewMembersListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            for (Triplet<Integer, Integer, Integer> cabin : data.cabins()) {
                CabinView c = player.getShip().getMapCabins().get(cabin.getValue0());
                if (cabin.getValue2() == 1) {
                    c.setCrewNumber(0);
                    c.setBrownAlien(false);
                    c.setPurpleAlien(false);
                }
                else {
                    c.setCrewNumber(cabin.getValue1());
                }
            }

            //TODO: notify
        };
        updateCrewMembersReceiver.registerListener(updateCrewMembersListener);

        CastEventReceiver<UpdateGoodsExchange> updateGoodsExchangeReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UpdateGoodsExchange> updateGoodsExchangeListener = data -> {

        };
        updateGoodsExchangeReceiver.registerListener(updateGoodsExchangeListener);
    }

    private PlayerDataView getPlayerDataView(String nickname) {
        if (MiniModel.getInstance().getClientPlayer().getUsername().equals(nickname)) {
            return MiniModel.getInstance().getClientPlayer();
        }

        return MiniModel.getInstance().getOtherPlayers().stream()
                .filter(p -> p.getUsername().equals(nickname))
                .findFirst()
                .orElse(null);
    }

    private int[] convertConnectors(List<Integer> connectors) {
        int[] result = new int[connectors.size()];
        for (int i = 0; i < connectors.size(); i++) result[i] = connectors.get(i);
        return result;
    }
}
