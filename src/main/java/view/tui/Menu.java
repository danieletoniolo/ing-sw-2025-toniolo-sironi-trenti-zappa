package view.tui;

import Model.Game.Lobby.LobbyInfo;
import view.tui.input.Command;
import view.tui.input.Parser;

import java.util.ArrayList;

enum MenuOption {
    CREATE,
    JOIN,
    EXIT,
    HELP;

    public static MenuOption from(String name) {
        try {
            return MenuOption.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

public class Menu {
    private ArrayList<LobbyInfo> lobbies;
    private Parser parser = new Parser();
    private Command command;
    private String nickname;

    public Menu(String nickname) {
        this.lobbies = new ArrayList<>();
        this.nickname = nickname;
    }

    public void drawMenu(){
        System.out.println("Welcome " + nickname + " in Space Trucker!");

        while(true) {
            System.out.println("\nMenu");
            drawLobbies();
            System.out.println("1. Create a new game with a lobby of 2/3/4 players");
            System.out.println("2. Join a game");
            System.out.println("3. Exit");


            Command command = parser.readCommand();
            MenuOption menuOption = MenuOption.from(command.name());
            if (menuOption == null) {
                System.out.println("Not a valid command. Please try again.");
                continue;
            }

            switch (menuOption) {
                case CREATE:
                    try {
                        lobbies.add(new LobbyInfo(nickname, Integer.parseInt(command.parameters()[0])));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case JOIN:
                    String name = String.join(" ", command.parameters());
                    for (LobbyInfo lobby : lobbies) {
                        if (lobby.getName().equals(name)) {
                            System.out.println("You are joining " + name);
                            TerminalUtils.clearTerminal();
                            //LobbyView lobbyView = new LobbyView(nickname, lobby.getTotalPlayers(), lobby.getName());
                            //lobbyView.drawLobby();
                        }
                    }
                    System.out.println("Lobby not found. Please try again.");
                    break;
                case EXIT:
                    System.out.println("Exiting game...");
                    parser.closeScanner();
                    System.exit(0);
                    break;
                case HELP:
                    System.out.println("To create a new game, type 'create' and the number of players.");
                    System.out.println("To join a game, type 'join' and the name of the lobby.");
                    System.out.println("To exit the game, type 'exit'.");
                    break;
            }
        }
    }

    private void drawLobbies() {
        System.out.println("Available lobbies:");
        for (LobbyInfo lobbyInfo : lobbies) {
            System.out.println(lobbyInfo.getName() + " - " + lobbyInfo.getNumberOfPlayersEntered() + "/" + lobbyInfo.getTotalPlayers());
        }
        if (lobbies.isEmpty()) {
            System.out.println("No lobbies available.");
        }
        System.out.println();
    }
}
