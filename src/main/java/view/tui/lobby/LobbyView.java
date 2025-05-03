package view.tui.lobby;

import view.tui.input.Command;
import view.tui.input.Parser;

import java.util.HashMap;
import java.util.Map;

enum PlayerStatus {
    READY,
    NOT,
    HELP,
    LEAVE;

    public static PlayerStatus from(String name) {
        try {
            return PlayerStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

public class LobbyView {
    private String nickname;
    private int lobbySize;
    private String lobbyName;
    private Map<String, Boolean> players;
    private Parser parser = new Parser();

    public LobbyView(String nickname, int lobbySize, String lobbyName) {
        this.nickname = nickname;
        this.lobbySize = lobbySize;
        this.lobbyName = lobbyName;
        this.players = new HashMap<>();
        players.put(nickname, false);
    }

    public void addPlayer(String player) {
        try {
            players.put(player, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void drawLobby() {

        while (true) {
            System.out.println("\nWelcome " + nickname + " to " + lobbyName);
            System.out.println("Lobby size: " + lobbySize);

            System.out.println("Players in the lobby: ");
            for (Map.Entry<String, Boolean> player : players.entrySet()) {
                System.out.println("- " + player.getKey() + " -> " + (player.getValue() ? "Ready" : "Not Ready"));
            }
            System.out.println("\nWaiting for players to join...");

            System.out.println("\nSet status (ready/not) or leave the lobby: ");
            Command command = parser.readCommand();
            PlayerStatus playerStatus = PlayerStatus.from(command.name());

            if (playerStatus == null) {
                System.out.println("Not a valid command. Please try again.");
                continue;
            }

            switch (playerStatus) {
                case READY:
                    players.put(nickname, true);
                    break;
                case NOT:
                    players.put(nickname, false);
                    break;
                case LEAVE:
                    if (!players.get(nickname)) {
                        System.out.println("Exiting lobby...");
                        return;
                    }else{
                        System.out.println("You are 'ready'. You cannot leave the lobby.");
                    }
                    break;
                case HELP:
                    System.out.println("To set your status, type 'ready' or 'not'.");
                    System.out.println("To exit the lobby, type 'back'.");
                    break;
            }
        }
    }
}
