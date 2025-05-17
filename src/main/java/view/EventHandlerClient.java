package view;

import controller.event.EventListener;
import controller.event.NetworkTransceiver;
import controller.event.game.AddCoins;
import controller.event.game.CanProtect;
import controller.event.lobby.CreateLobby;
import controller.event.lobby.JoinLobby;
import controller.event.lobby.LeaveLobby;
import view.structures.MiniModel;
import view.structures.board.LevelView;
import view.structures.lobby.LobbyView;


public class EventHandlerClient {
    NetworkTransceiver transceiver;
    Manager manager;

    private final EventListener<CreateLobby> createLobbyListener = data -> {
        /*LevelView level = LevelView.valueOf(data.level().name());
        LobbyView lobbyView = new LobbyView(data.lobbyID(), data.maxPlayers(), level);

        MiniModel.getInstance().lobbyViews.add(lobbyView);

        manager.notifyCreateLobby(data);*/
    };

    private final EventListener<JoinLobby> joinLobbyListener = data -> {
        MiniModel.getInstance().lobbyViews.stream()
                .filter(lobbyView -> lobbyView.getLobbyName().equals(data.lobbyID()))
                .findFirst()
                .ifPresent(lobbyView -> {
                    lobbyView.addPlayer(data.userID());
                });
        manager.notifyJoinLobby(data);

    };

    private final EventListener<LeaveLobby> leaveLobbyListener = data -> {
        /*MiniModel.getInstance().lobbyViews.stream()
                .filter(lobbyView -> lobbyView.getLobbyName().equals(data.lobbyID()))
                .findFirst()
                .ifPresent(lobbyView -> {
                    lobbyView.removePlayer(data.userID());
                });
        manager.notifyLeaveLobby(data);*/
    };

    private final EventListener<AddCoins> addCoinsListener = data -> {
        MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(data.userID()))
                .findFirst()
                .ifPresent(player -> {
                    player.setCoins(data.coins());
                });


    };

    private final EventListener<CanProtect> canProtectListener = data -> {
        //MiniModel.getInstance().
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
