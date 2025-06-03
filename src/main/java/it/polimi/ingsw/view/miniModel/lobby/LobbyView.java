package it.polimi.ingsw.view.miniModel.lobby;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.board.LevelView;

import java.util.HashMap;
import java.util.Map;

public class LobbyView implements Structure {
    private String Dash = "─";
    private String Bow1 = "╭";
    private String Bow2 = "╮";
    private String Bow3 = "╰";
    private String Bow4 = "╯";
    private String Vertical = "│";

    private Map<String, Boolean> players;
    private final String lobbyName;
    private final int maxPlayer;
    private final LevelView level;
    private int numberOfPlayers;

    public LobbyView(String lobbyName, int numberOfPlayers, int maxPlayer, LevelView level) {
        players = new HashMap<>();
        this.lobbyName = lobbyName;
        this.maxPlayer = maxPlayer;
        this.numberOfPlayers = numberOfPlayers;
        this.level = level;
    }

    public LevelView getLevel() {
        return level;
    }

    public void addPlayer(String playerName) {
        players.put(playerName, false);
        numberOfPlayers++;
    }

    public void removePlayer(String playerName) {
        players.remove(playerName);
        numberOfPlayers--;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setPlayerStatus(String playerName, boolean status) {
        players.put(playerName, status);
    }

    public Map<String, Boolean> getPlayers() {
        return players;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the lobby here
    }

    public static int getRowsToDraw() {
        return 10;
    }

    public String drawLineTui(int line) {
        StringBuilder str = new StringBuilder();
        String distance = "   ";
        int width = 2 + Math.max(distance.length() + lobbyName.length() + distance.length(), players.keySet().stream().mapToInt(String::length).max().orElse(0) + (" - Not Ready ").length());

        if (line == 0) {
            str.append(Bow1);
            str.append(String.valueOf(Dash).repeat(Math.max(0, width - 1)));
            str.append(Bow2);
            return str.toString();
        }

        if (line == getRowsToDraw() - 1){
            str.append(Bow3);
            str.append(String.valueOf(Dash).repeat(Math.max(0, width - 1)));
            str.append(Bow4);
            return str.toString();
        }

        if (line == 1){
            if (lobbyName.length() == width) {
                str.append(Vertical).append(distance).append(lobbyName).append(distance).append(Vertical);
                return str.toString();
            }

            int tmp = (width - lobbyName.length()) / 2 - 1;
            str.append(Vertical).append(" ".repeat(tmp));
            str.append(lobbyName);
            while (str.length() <= width - 1) {
                str.append(" ");
            }
            str.append(Vertical);
            return str.toString();
        }

        if (line >= 3 && line < players.size() + 3) {
            String name = players.keySet().stream().toList().get(line - 3);
            boolean status = players.get(name);

            str.append(Vertical).append(" ").append(name).append(" - ").append(status ? "Ready" : "Not Ready");
            while (str.length() <= width - 2) {
                str.append(" ");
            }
            str.append(" ").append(Vertical);
            return str.toString();
        }

        if (line == getRowsToDraw() - 2) {
            str.append(Vertical);
            str.append(String.valueOf(" ").repeat(width/2 - 1));
            str.append(numberOfPlayers).append("/").append(maxPlayer);
            str.append(String.valueOf(" ").repeat(width/2 - 2));
            str.append(Vertical);
            return str.toString();
        }

        str.append(Vertical);
        str.append(String.valueOf(" ").repeat(width - 1));
        str.append(Vertical);
        return str.toString();
    }
}
