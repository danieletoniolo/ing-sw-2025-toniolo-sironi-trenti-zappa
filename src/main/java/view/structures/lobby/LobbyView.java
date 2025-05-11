package view.structures.lobby;

import java.util.HashMap;
import java.util.Map;

public class LobbyView {
    public static String Dash = "─";
    public static String Bow1 = "╭";
    public static String Bow2 = "╮";
    public static String Bow3 = "╰";
    public static String Bow4 = "╯";
    public static String Vertical = "│";

    private Map<String, Boolean> players;
    private String lobbyName;
    private int maxPlayer;

    public LobbyView(String lobbyName, int maxPlayer) {
        players = new HashMap<>();
        this.lobbyName = lobbyName;
        this.maxPlayer = maxPlayer;

    }

    public void addPlayer(String playerName) {
        players.put(playerName, false);
    }

    public void removePlayer(String playerName) {
        players.remove(playerName);
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

    public int getRowsToDraw() {
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
            str.append(String.valueOf(" ").repeat(width/2 - 2));
            str.append(players.size()).append("/").append(maxPlayer);
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
