package view.structures;

import view.structures.board.BoardView;
import view.structures.components.ComponentView;
import view.structures.deck.DeckView;
import view.structures.lobby.LobbyView;
import view.structures.logIn.LogInView;
import view.structures.player.PlayerDataView;

import java.util.ArrayList;

public class MiniModel {
    private static MiniModel instance;

    public LogInView logInView;
    public ArrayList<LobbyView> lobbyViews;
    public ArrayList<DeckView> deckViews;
    public BoardView boardView;
    public ArrayList<PlayerDataView> players;
    public ArrayList<ComponentView> components;

    public static MiniModel getInstance() {
        if (instance == null) {
            instance = new MiniModel();
        }
        return instance;
    }
}
