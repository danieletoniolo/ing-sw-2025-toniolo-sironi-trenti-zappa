package it.polimi.ingsw.view;


import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.game.serverToClient.*;
import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import org.javatuples.Pair;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.BatteryView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.countDown.CountDown;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;


public class EventHandlerClient {
    NetworkTransceiver transceiver;
    Manager manager;

    public EventHandlerClient(NetworkTransceiver transceiver, Manager manager) {
        this.transceiver = transceiver;
        this.manager = manager;


        /**
         * Set the userID of the client
         */
        CastEventReceiver<UserIDSet> userIDSetReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<UserIDSet> userIDSetListener = data -> {
            MiniModel mm = MiniModel.getInstance();
            mm.userID = data.userID();

            manager.notifyUserIDSet();
        };
        userIDSetReceiver.registerListener(userIDSetListener);

        /**
         * Set the nickname of the player
         */
        CastEventReceiver<NicknameSet> nicknameSetReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<NicknameSet> nicknameSetListener = data -> {
            MiniModel.getInstance().nickname = data.nickname();

            manager.notifyNicknameSet();
        };
        nicknameSetReceiver.registerListener(nicknameSetListener);

        // Lobby events
        /**
         * Set all the lobbies
         */
        CastEventReceiver<Lobbies> lobbiesReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<Lobbies> lobbiesListener = data -> {
            for (int i = 0; i < data.lobbiesNames().size(); i++) {
                MiniModel.getInstance().lobbiesView.add(new LobbyView(data.lobbiesNames().get(i), data.lobbiesPlayers().get(i).getValue0(),
                        data.lobbiesPlayers().get(i).getValue1(), LevelView.fromValue(data.lobbiesLevels().get(i))));
            }

            manager.notifyLobbies();
        };
        lobbiesReceiver.registerListener(lobbiesListener);

        /**
         * Create a new lobby, if it is created by this client it is set as tha main current lobby
         */
        CastEventReceiver<LobbyCreated> lobbyCreatedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyCreated> lobbyCreatedListener = data -> {
            LobbyView lobby = new LobbyView(data.lobbyID(), 1, data.maxPlayers(), LevelView.fromValue(data.level()));
            MiniModel.getInstance().lobbiesView.add(lobby);
            if (data.nickname().equals(MiniModel.getInstance().nickname)) {
                MiniModel.getInstance().currentLobby = lobby;
            }

            manager.notifyCreatedLobby(data);
        };
        lobbyCreatedReceiver.registerListener(lobbyCreatedListener);

        /**
         * Add a new player inside a specific lobby
         */
        CastEventReceiver<LobbyJoined> lobbyJoinedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyJoined> lobbyJoinedListener = data -> {
            MiniModel.getInstance().lobbiesView.stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> {
                        lobby.addPlayer(data.nickname());
                    });

            manager.notifyLobbyJoined(data);
        };
        lobbyJoinedReceiver.registerListener(lobbyJoinedListener);

        /**
         * Remove a player from a specific lobby
         */
        CastEventReceiver<LobbyLeft> lobbyLeftReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyLeft> lobbyLeftListener = data -> {
            MiniModel.getInstance().lobbiesView.stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> {
                        lobby.removePlayer(data.nickname());
                    });

            manager.notifyLobbyLeft(data);
        };
        lobbyLeftReceiver.registerListener(lobbyLeftListener);

        /**
         * Remove a lobby
         */
        CastEventReceiver<LobbyRemoved> lobbyRemovedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<LobbyRemoved> lobbyRemovedListener = data -> {
            MiniModel.getInstance().lobbiesView.stream()
                    .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                    .findFirst()
                    .ifPresent(lobby -> {
                        MiniModel.getInstance().lobbiesView.remove(lobby);
                    });

            manager.notifyLobbyRemoved(data);
        };
        lobbyRemovedReceiver.registerListener(lobbyRemovedListener);

        /**
         * Start a countdown to announce that the game is starting
         */
        CastEventReceiver<StartingGame> startingGameReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<StartingGame> startingGameListener = data -> {
            new Thread(() -> {
                MiniModel.getInstance().countDown = new CountDown();
                int time = 3;
                while (time >= 0) {
                    try {
                        MiniModel.getInstance().countDown.setSecondsRemaining(time);
                        manager.notifyCountDown();
                        Thread.sleep(1000);
                        time--;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                MiniModel.getInstance().countDown = null;
                manager.notifyStartingGame();
            }).start();

        };

        // Game events
        /**
         * Notify the best looking ships
         */
        CastEventReceiver<BestLookingShips> bestLookingShipsReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<BestLookingShips> bestLookingShipsListener = data -> {

            manager.notifyBestLookingShips(data);
        };
        bestLookingShipsReceiver.registerListener(bestLookingShipsListener);

        /**
         * Remove a battery from the tiles in the batteriesIDs list
         */
        CastEventReceiver<CannonsUsed> cannonsUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<CannonsUsed> cannonsUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Integer id : data.batteriesIDs()) {
                    ((BatteryView) player.getShip().getMapBatteries().get(id)).setNumberOfBatteries(
                            ((BatteryView) player.getShip().getMapBatteries().get(id)).getNumberOfBatteries() - 1
                    );
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


        /**
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

        /**
         * RRemove a battery from the tiles in the batteriesIDs list
         */
        CastEventReceiver<EnginesUsed> enginesUsedReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<EnginesUsed> enginesUsedListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());

            if (player != null) {
                for (Integer id : data.batteriesIDs()) {
                    ((BatteryView) player.getShip().getMapBatteries().get(id)).setNumberOfBatteries(
                            ((BatteryView) player.getShip().getMapBatteries().get(id)).getNumberOfBatteries() - 1
                    );
                }
            }
            manager.notifyEnginesUsed(data);
        };
        enginesUsedReceiver.registerListener(enginesUsedListener);

        /**
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

        // TODO: GoodsSwap
        // TODO: MinPlayer

        /**
         * Move the marker of the player on the board
         */
        CastEventReceiver<MoveMarker> moveMarkerReceiver = new CastEventReceiver<>(this.transceiver);
        EventListener<MoveMarker> moveMarkerListener = data -> {
            PlayerDataView player = getPlayerDataView(data.nickname());
            MiniModel.getInstance().boardView.movePlayer(player.getMarkerView(), data.steps());
        };
        moveMarkerReceiver.registerListener(moveMarkerListener);

        /// Building events

    }

    private PlayerDataView getPlayerDataView(String nickname) {
        if (MiniModel.getInstance().currentPlayer.getUsername().equals(nickname)) {
            return MiniModel.getInstance().currentPlayer;
        }

        return MiniModel.getInstance().otherPlayers.stream()
                .filter(p -> p.getUsername().equals(nickname))
                .findFirst()
                .orElse(null);
    }
}
