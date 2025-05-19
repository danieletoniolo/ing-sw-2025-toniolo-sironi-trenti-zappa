package view;

import event.EventListener;
import event.NetworkTransceiver;
import event.game.AddCoins;
import event.game.AddLoseCrew;
import event.lobby.CreateLobby;
import event.lobby.JoinLobby;
import event.lobby.LeaveLobby;
import event.lobby.RemoveLobby;
import view.structures.MiniModel;
import view.structures.board.LevelView;
import view.structures.lobby.LobbyView;


public class EventHandlerClient {
    NetworkTransceiver transceiver;
    Manager manager;

    // Lobby events
    private final EventListener<CreateLobby> createLobbyListener = data -> {
        // Create a new lobby view and add it to the MiniModel
        LevelView level = LevelView.valueOf(data.level().name());
        LobbyView lobbyView = new LobbyView(data.lobbyID(), data.maxPlayers(), level);

        MiniModel.getInstance().lobbyViews.add(lobbyView);

        manager.notifyCreateLobby(data);
    };

    private final EventListener<RemoveLobby> removeLobbyListener = data -> {
        // Remove the lobby view from the MiniModel
        MiniModel.getInstance().lobbyViews.removeIf(lobbyView -> lobbyView.getLobbyName().equals(data.lobbyID()));

        manager.notifyRemoveLobby(data);
    };

    private final EventListener<JoinLobby> joinLobbyListener = data -> {
        // Add the player to the specific lobby
        MiniModel.getInstance().lobbyViews.stream()
                .filter(lobbyView -> lobbyView.getLobbyName().equals(data.lobbyID()))
                .findFirst()
                .ifPresent(lobbyView -> {
                    lobbyView.addPlayer(data.userID());
                });

        manager.notifyJoinLobby(data);
    };

    private final EventListener<LeaveLobby> leaveLobbyListener = data -> {
        // Remove the player from the specific lobby
        MiniModel.getInstance().lobbyViews.stream()
                .filter(lobbyView -> lobbyView.getLobbyName().equals(data.lobbyID()))
                .findFirst()
                .ifPresent(lobbyView -> {
                    lobbyView.removePlayer(data.userID());
                });
        manager.notifyLeaveLobby(data);
    };


    // Game events
    private final EventListener<AddCoins> addCoinsListener = data -> {
        MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(data.userID()))
                .findFirst()
                .ifPresent(player -> {
                    player.setCoins(data.coins());
                });

        manager.notifyAddCoins(data);
    };

    private final EventListener<AddLoseCrew> addLoseCrewListener = data -> {

    };



    public EventHandlerClient(NetworkTransceiver transceiver, Manager manager) {
        this.transceiver = transceiver;
        this.manager = manager;

        // Register listeners for events
        // Lobby events
        this.transceiver.registerListener(createLobbyListener);
        this.transceiver.registerListener(joinLobbyListener);
        this.transceiver.registerListener(leaveLobbyListener);

        // Game events
        this.transceiver.registerListener(addCoinsListener);

    };
}
