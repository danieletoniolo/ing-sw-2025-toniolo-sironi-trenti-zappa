package view;


import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.game.serverToClient.BestLookingShips;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.lobby.LobbyView;


public class EventHandlerClient {
    NetworkTransceiver transceiver;
    Manager manager;

    private final EventListener<NicknameSet> nicknameSetListener = data -> {
        MiniModel.getInstance().nickname = data.nickname();

        manager.notifyNicknameSet();
    };

    // Lobby events
    private final EventListener<Lobbies> lobbiesListener = data -> {
        //TODO: Modificare LevelView
        for (int i = 0; i < data.lobbiesNames().size(); i++) {
            MiniModel.getInstance().lobbiesView.add(new LobbyView(data.lobbiesNames().get(i), data.lobbiesPlayers().get(i).getValue0(), data.lobbiesPlayers().get(i).getValue1(), LevelView.fromValue(0)));
        }

        manager.notifyLobbies();
    };

    private final EventListener<LobbyCreated> lobbyCreatedListener = data -> {
        //TODO: Modificare LevelView
        LobbyView lobby = new LobbyView(data.lobbyID(), 1, data.maxPlayers(), LevelView.fromValue(0));
        MiniModel.getInstance().lobbiesView.add(lobby);
        if (data.nickname().equals(MiniModel.getInstance().nickname)) {
            MiniModel.getInstance().currentLobby = lobby;
        }

        manager.notifyCreatedLobby(data);
    };

    private final EventListener<LobbyJoined> lobbyJoinedListener = data -> {
        MiniModel.getInstance().lobbiesView.stream()
                .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                .findFirst()
                .ifPresent(lobby -> {
                    lobby.addPlayer(data.nickname());
                });

        manager.notifyLobbyJoined(data);
    };

    private final EventListener<LobbyLeft> lobbyLeftListener = data -> {
        MiniModel.getInstance().lobbiesView.stream()
                .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                .findFirst()
                .ifPresent(lobby -> {
                    lobby.removePlayer(data.nickname());
                });

        manager.notifyLobbyLeft(data);
    };

    private final EventListener<LobbyRemoved> lobbyRemovedListener = data -> {
        MiniModel.getInstance().lobbiesView.stream()
                .filter(lobby -> lobby.getLobbyName().equals(data.lobbyID()))
                .findFirst()
                .ifPresent(lobby -> {
                    MiniModel.getInstance().lobbiesView.remove(lobby);
                });

        manager.notifyLobbyRemoved(data);
    };

    // Game events
    private final EventListener<BestLookingShips> bestLookingShipsListener = data -> {

        manager.notifyBestLookingShips(data);
    };

    public EventHandlerClient(NetworkTransceiver transceiver, Manager manager) {
        this.transceiver = transceiver;
        this.manager = manager;

        CastEventReceiver<NicknameSet> nicknameSetReceiver = new CastEventReceiver<>(this.transceiver);
        nicknameSetReceiver.registerListener(nicknameSetListener);

        // Register listeners for events
        // Lobby events
        CastEventReceiver<Lobbies> lobbiesReceiver = new CastEventReceiver<>(this.transceiver);
        lobbiesReceiver.registerListener(lobbiesListener);

        CastEventReceiver<LobbyCreated> lobbyCreatedReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyCreatedReceiver.registerListener(lobbyCreatedListener);

        CastEventReceiver<LobbyJoined> lobbyJoinedReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyJoinedReceiver.registerListener(lobbyJoinedListener);

        CastEventReceiver<LobbyLeft> lobbyLeftReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyLeftReceiver.registerListener(lobbyLeftListener);

        CastEventReceiver<LobbyRemoved> lobbyRemovedReceiver = new CastEventReceiver<>(this.transceiver);
        lobbyRemovedReceiver.registerListener(lobbyRemovedListener);

        // Game events
        CastEventReceiver<BestLookingShips> bestLookingShipsReceiver = new CastEventReceiver<>(this.transceiver);
    };
}
