package it.polimi.ingsw.view.miniModel.lobby;

import it.polimi.ingsw.view.gui.controllers.misc.LobbyBoxController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a view of a lobby in the mini model.
 * Implements Structure and MiniModelObservable to support observer pattern.
 */
public class LobbyView implements Structure, MiniModelObservable {
    /** Map of player names to their ready status. */
    private final Map<String, Boolean> players;
    /** Name of the lobby. */
    private final String lobbyName;
    /** Display name including lobby and level. */
    private final String nameLevel;
    /** Maximum number of players allowed in the lobby. */
    private final int maxPlayer;
    /** The level associated with this lobby. */
    private final LevelView level;
    /** Current number of players in the lobby. */
    private int numberOfPlayers;
    /** List of observers registered to this lobby view. */
    private final List<MiniModelObserver> observers;

    /**
     * Constructs a LobbyView with the specified parameters.
     *
     * @param lobbyName        the name of the lobby
     * @param numberOfPlayers  the current number of players
     * @param maxPlayer        the maximum number of players
     * @param level            the level associated with the lobby
     */
    public LobbyView(String lobbyName, int numberOfPlayers, int maxPlayer, LevelView level) {
        players = new HashMap<>();
        this.lobbyName = lobbyName;
        this.nameLevel = lobbyName + " - " + level.toString();
        this.maxPlayer = maxPlayer;
        this.numberOfPlayers = numberOfPlayers;
        this.level = level;
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to this lobby view.
     *
     * @param observer the observer to register
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from this lobby view.
     *
     * @param observer the observer to unregister
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers of a change.
     */
    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    /**
     * Loads and returns the JavaFX Node representing this lobby.
     *
     * @return the Node for the lobby, or null if loading fails
     */
    public Node getNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/misc/lobbyBox.fxml"));
            Node root = loader.load();

            LobbyBoxController controller = loader.getController();
            controller.setModel(this);

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the level associated with this lobby.
     *
     * @return the LevelView object representing the level
     */
    public LevelView getLevel() {
        return level;
    }

    /**
     * Adds a player to the lobby and notifies observers.
     *
     * @param playerName the name of the player to add
     */
    public void addPlayer(String playerName) {
        players.put(playerName, false);
        numberOfPlayers++;
        notifyObservers();
    }

    /**
     * Removes a player from the lobby and notifies observers.
     *
     * @param playerName the name of the player to remove
     */
    public void removePlayer(String playerName) {
        players.remove(playerName);
        numberOfPlayers--;
        notifyObservers();
    }

    /**
     * Returns the current number of players in the lobby.
     *
     * @return the number of players
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Sets the ready status of a player.
     *
     * @param playerName the name of the player
     * @param status     true if the player is ready, false otherwise
     */
    public void setPlayerStatus(String playerName, boolean status) {
        players.put(playerName, status);
    }

    /**
     * Returns a map of player names to their ready status.
     *
     * @return a map of player names and their ready status
     */
    public Map<String, Boolean> getPlayers() {
        return players;
    }

    /**
     * Returns the name of the lobby.
     *
     * @return the lobby name
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Returns the maximum number of players allowed in the lobby.
     *
     * @return the maximum number of players
     */
    public int getMaxPlayer() {
        return maxPlayer;
    }

    /**
     * Returns the number of rows to draw in the TUI representation.
     *
     * @return the number of rows to draw
     */
    public static int getRowsToDraw() {
        return 10;
    }

    /**
     * Draws a specific line of the lobby in TUI format.
     *
     * @param line the line number to draw
     * @return the string representing the line in TUI
     */
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
