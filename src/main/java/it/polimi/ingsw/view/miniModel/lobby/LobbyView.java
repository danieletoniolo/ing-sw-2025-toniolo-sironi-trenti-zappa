package it.polimi.ingsw.view.miniModel.lobby;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.board.LevelView;

import java.util.HashMap;
import java.util.Map;

public class LobbyView implements Structure {
    private final Map<String, Boolean> players;
    private final String lobbyName;
    private final String nameLevel;
    private final int maxPlayer;
    private final LevelView level;
    private int numberOfPlayers;

    public LobbyView(String lobbyName, int numberOfPlayers, int maxPlayer, LevelView level) {
        players = new HashMap<>();
        this.lobbyName = lobbyName;
        this.nameLevel = lobbyName + " - " + level.toString();
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

    public static int getRowsToDraw() {
        return 10;
    }

    public String drawLineTui(int line) {
        String Dash = "─";
        String Bow1 = "╭";
        String Bow2 = "╮";
        String Bow3 = "╰";
        String Bow4 = "╯";
        String Vertical = "│";

        StringBuilder str = new StringBuilder();
        String distance = "   ";
        int width = 2 + Math.max(distance.length() + nameLevel.length() + distance.length(), players.keySet().stream().mapToInt(String::length).max().orElse(0) + (" - Not Ready ").length());

        if (line == 0) {
            str.append(Bow1);
            str.append((Dash).repeat(Math.max(0, width - 1)));
            str.append(Bow2);
            return str.toString();
        }

        if (line == getRowsToDraw() - 1){
            str.append(Bow3);
            str.append((Dash).repeat(Math.max(0, width - 1)));
            str.append(Bow4);
            return str.toString();
        }

        if (line == 1){
            if (nameLevel.length() == width) {
                str.append(Vertical).append(distance).append(nameLevel).append(distance).append(Vertical);
                return str.toString();
            }

            int tmp = (width - nameLevel.length()) / 2 - 1;
            str.append(Vertical).append(" ".repeat(tmp));
            str.append(nameLevel);
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
            str.append((" ").repeat((width/2 - 2) % 2 == 0 ? (width/2 - 2) : (width/2 - 1)));
            str.append(numberOfPlayers).append("/").append(maxPlayer);
            while (str.length() < width) {
                str.append(" ");
            }
            str.append(Vertical);
            return str.toString();
        }

        str.append(Vertical);
        str.append((" ").repeat(width - 1));
        str.append(Vertical);
        return str.toString();
    }
}
