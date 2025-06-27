package it.polimi.ingsw.view.miniModel.board;

import it.polimi.ingsw.view.gui.controllers.board.BoardController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View component for the game board, implementing both Structure and MiniModelObservable interfaces.
 * Manages the visual representation and state of the game board including player positions,
 * level-specific configurations, and board layout rendering.
 */
public class BoardView implements Structure, MiniModelObservable {
    /** Array representing the board path with positions marked by strings */
    private String[] path;

    /** Map storing player markers and their current positions on the board */
    private final Map<MarkerView, Integer> players;

    /** Total number of players in the game */
    private final int numberOfPlayers;

    /** Current level of the game (LEARNING or SECOND) */
    private final LevelView level;

    /** Number of steps required to complete one full lap around the board */
    private int stepsForALap;

    /** Unicode character for upward arrow direction indicator */
    public static String ArrowUp = "↑";

    /** Unicode character for downward arrow direction indicator */
    public static String ArrowDown = "↓";

    /** Unicode character for leftward arrow direction indicator */
    public static String ArrowLeft = "←";

    /** Unicode character for rightward arrow direction indicator */
    public static String ArrowRight = "→";

    /** Unicode character for horizontal line/dash in board drawing */
    public static String Dash = "─";

    /** Unicode character for top-left corner bow in board drawing */
    public static String Bow1 = "╭";

    /** Unicode character for top-right corner bow in board drawing */
    public static String Bow2 = "╮";

    /** Unicode character for bottom-left corner bow in board drawing */
    public static String Bow3 = "╰";

    /** Unicode character for bottom-right corner bow in board drawing */
    public static String Bow4 = "╯";

    /** Timer view component for the second level */
    private TimerView timerView;
    /** The decks are stored in a Pair: The first element is the deck views, and the second element is a boolean array.
     If boolean[i] == true, the deck[i] is not taken by a player, else the deck is taken and not viewable on the building screen*/
    private Pair<DeckView[], Boolean[]> decksView;
    /** List of observers that will be notified when the board state changes */
    private final List<MiniModelObserver> observers;

    /** Cached JavaFX node and controller pair for the board view to avoid reloading FXML */
    private Pair<Node, BoardController> boardPair;

    /**
     * Constructs a new BoardView for the specified level and number of players.
     * Initializes the board configuration, player tracking, and level-specific components.
     *
     * @param level the game level (LEARNING or SECOND) which determines board size and features
     * @param numberOfPlayers the total number of players in the game
     */
    public BoardView(LevelView level, int numberOfPlayers) {
        this.level = level;
        switch (level) {
            case LEARNING:
                this.stepsForALap = 18;
                break;
            case SECOND:
                this.stepsForALap = 24;
                this.timerView = new TimerView();
                decksView = new Pair<>(new DeckView[3], new Boolean[3]);
                break;
        }
        this.players = new HashMap<>();
        this.observers = new ArrayList<>();
        this.numberOfPlayers = numberOfPlayers;
        initializeBoard();
    }

    /**
     * Registers a new observer to be notified when the board state changes.
     * This method is thread-safe and ensures observers are added without conflicts.
     *
     * @param observer the MiniModelObserver to register for notifications
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from receiving board state change notifications.
     * This method is thread-safe and ensures observers are removed without conflicts.
     *
     * @param observer the MiniModelObserver to unregister from notifications
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers about changes in the board state.
     * This method is thread-safe and iterates through all observers to trigger their react() method.
     * Used to update UI components and other dependent views when the board model changes.
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
     * Returns the Node representing the board view.
     * This method loads the FXML file for the board and sets the controller with this model.
     *
     * @return Node representing the board view, or null if an error occurs during loading.
     */
    public Pair<Node, BoardController> getNode() {
        try {
            if (boardPair != null) return boardPair;

            String path = level == LevelView.LEARNING ? "/fxml/board/learningBoard.fxml" : "/fxml/board/secondBoard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            BoardController controller = loader.getController();
            controller.setModel(this);

            boardPair = new Pair<>(root, controller);
            return boardPair;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the number of steps required to complete a full lap on the board.
     * The value depends on the level: 18 steps for LEARNING level, 24 steps for SECOND level.
     *
     * @return the number of steps for a complete lap
     */
    public int getStepsForALap() {
        return stepsForALap;
    }

    /**
     * Returns the timer view component for the second level.
     * The timer is only available when playing at the SECOND level.
     *
     * @return the TimerView instance, or null if playing at LEARNING level
     */
    public TimerView getTimerView() {
        return timerView;
    }

    /**
     * Returns the deck views and their availability status.
     * The first element contains the deck views array, the second element contains
     * a boolean array where true indicates the deck is available (not taken by a player).
     *
     * @return a Pair containing DeckView array and Boolean array indicating deck availability
     */
    public Pair<DeckView[], Boolean[]> getDecksView() {
        return decksView;
    }

    /**
     * Returns a list of all player positions on the board.
     * Each entry contains a player marker and their current position as a step number.
     *
     * @return a List of Pairs containing MarkerView and their corresponding board positions
     */
    public List<Pair<MarkerView, Integer>> getPlayerPositions() {
        return players.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Returns the number of rows needed to draw the board in text-based UI.
     * The number varies by level: 9 rows for LEARNING level, 13 rows for SECOND level.
     *
     * @return the number of rows required for board visualization
     */
    public int getRowsToDraw() {
        return level == LevelView.LEARNING ? 9 : 13;
    }

    /**
     * Calculates and returns the number of columns needed to draw the board in text-based UI.
     * The calculation is based on the board size and includes space for arrows and formatting.
     *
     * @return the number of columns required for board visualization
     */
    public int getColsToDraw() {
        return (stepsForALap / 4 + 1) * 7 + 12;
    }

    /**
     * Moves a player to a specific position on the board.
     * Reinitializes the board, updates the player's position, and notifies observers.
     *
     * @param color the MarkerView representing the player to move
     * @param step the target position (step number) on the board
     */
    public void movePlayer(MarkerView color, int step) {
        initializeBoard();

        players.put(color, step);
        players.forEach((key, value) -> {
            path[value] = key.drawTui();
        });
        notifyObservers();
    }

    /**
     * Removes a player from the board and updates the visual representation.
     * Reinitializes the board, removes the specified player from the players map,
     * updates the path with remaining players' positions, and notifies observers.
     *
     * @param color the MarkerView representing the player to remove from the board
     */
    public void removePlayer(MarkerView color) {
        initializeBoard();

        players.remove(color);
        players.forEach((key, value) -> {
            path[value] = key.drawTui();
        });
        notifyObservers();
    }

    /**
     * Returns the total number of players configured for this game.
     * This value is set during BoardView construction and remains constant throughout the game.
     *
     * @return the total number of players in the game
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Initializes the board path array with default values and level-specific numbered positions.
     * Sets up the board configuration based on the current level (LEARNING or SECOND),
     * placing numbered positions at predetermined locations on the board path.
     * For LEARNING level: positions 4,3,2,1 at steps 0,1,2,4 respectively.
     * For SECOND level: positions 4,3,2,1 at steps 0,1,3,6 respectively.
     */
    private void initializeBoard() {
        switch (level) {
            case LEARNING:
                this.path = new String[this.stepsForALap];
                for (int i = 0; i < stepsForALap; i++) this.path[i] = " ";
                this.path[0] = "4";
                this.path[1] = "3";
                this.path[2] = "2";
                this.path[4] = "1";
                break;
            case SECOND:
                this.path = new String[stepsForALap];
                for (int i = 0; i < this.stepsForALap; i++) this.path[i] = " ";
                this.path[0] = "4";
                this.path[1] = "3";
                this.path[3] = "2";
                this.path[6] = "1";
                break;
            default: throw new IllegalStateException("Unexpected value: " + level);
        }
    }

    /**
     * Returns the current level of the game.
     * This level determines the board size, layout, and available features.
     *
     * @return the LevelView representing the current game level (LEARNING or SECOND)
     */
    public LevelView getLevel() {
        return level;
    }

    /**
     * Draws a specific line of the board in text-based user interface format.
     * This method generates ASCII art representation of the board line by line,
     * including borders, arrows, player positions, and level-specific formatting.
     *
     * @param line the line number to draw (0-based index from top to bottom)
     * @return a String representing the visual representation of the specified board line
     */
    @Override
    public String drawLineTui(int line) {
        StringBuilder print = new StringBuilder();
        int cols = stepsForALap/4 + 1;

        if (line % 2 == 0) {
            if (line == 0) {
                int up = 0;
                print.append("  ").append(Bow1).append(Dash).append(Dash);
                for (int i = 0; i < cols; i++) {
                    print.append(Dash).append(ArrowRight).append("[ ").append(path[up]).append(" ]");
                    up++;
                }
                print.append(Dash).append(Dash).append(Dash).append(ArrowRight).append(Bow2).append("  ");
                return print.toString();
            }
            if (line == getRowsToDraw() - 1) {
                int down = stepsForALap - (level == LevelView.LEARNING ? 4 : 6);
                print.append("  ").append(Bow3).append(ArrowLeft).append(Dash).append(Dash).append(Dash);
                for (int i = 0; i < cols; i++) {
                    print.append("[ ").append(path[down]).append(" ]").append(ArrowLeft).append(Dash);
                    down--;
                }
                print.append(Dash).append(Dash).append(Bow4).append("  ");
                return print.toString();
            }
            if (line == getRowsToDraw()/2) {
                print.append("[ ").append(path[stepsForALap - line / 2]).append(" ]  ");
                print.append(level == LevelView.LEARNING ? "            LEARNING               " : "                    SECOND                       ");
                print.append("[ ").append(path[stepsForALap / 4 + line / 2]).append(" ]");
                return print.toString();
            }
            print.append("[ ").append(path[stepsForALap - line / 2]).append(" ]  ");
            print.append("       ".repeat(Math.max(0, cols)));
            print.append("[ ").append(path[stepsForALap / 4 + line / 2]).append(" ]");
            return print.toString();
        }
        print.append("  ").append(ArrowUp).append("  ");
        print.append("       ".repeat(Math.max(0, cols)));
        print.append("    ").append(ArrowDown).append("  ");
        return print.toString();
    }
}
