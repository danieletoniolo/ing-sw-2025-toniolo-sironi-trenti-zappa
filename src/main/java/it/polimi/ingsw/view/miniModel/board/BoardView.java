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

public class BoardView implements Structure, MiniModelObservable {
    private String[] path;
    private final Map<MarkerView, Integer> players;
    private final int numberOfPlayers;
    private final LevelView level;
    private int stepsForALap;
    public static String ArrowUp = "↑";
    public static String ArrowDown = "↓";
    public static String ArrowLeft = "←";
    public static String ArrowRight = "→";
    public static String Dash = "─";
    public static String Bow1 = "╭";
    public static String Bow2 = "╮";
    public static String Bow3 = "╰";
    public static String Bow4 = "╯";
    private TimerView timerView;
    /** The decks are stored in a Pair: The first element is the deck views, and the second element is a boolean array.
     If boolean[i] == true, the deck[i] is not taken by a player, else the deck is taken and not viewable on the building screen*/
    private Pair<DeckView[], Boolean[]> decksView;
    private final List<MiniModelObserver> observers;
    private Pair<Node, BoardController> node;


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

    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

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
            if (node != null) {
                return node;
            }
            String path = level == LevelView.LEARNING ? "/fxml/board/learningBoard.fxml" : "/fxml/board/secondBoard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            BoardController controller = loader.getController();
            controller.setModel(this);

            return new Pair<>(root, controller);
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

    public TimerView getTimerView() {
        return timerView;
    }

    public Pair<DeckView[], Boolean[]> getDecksView() {
        return decksView;
    }

    public List<Pair<MarkerView, Integer>> getPlayerPositions() {
        return players.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .toList();
    }

    public int getRowsToDraw() {
        return level == LevelView.LEARNING ? 9 : 13;
    }

    public int getColsToDraw() {
        return (stepsForALap / 4 + 1) * 7 + 12;
    }

    public void movePlayer(MarkerView color, int step) {
        initializeBoard();

        players.put(color, step);
        players.forEach((key, value) -> {
            path[value] = key.drawTui();
        });
        notifyObservers();
    }

    public void removePlayer(MarkerView color) {
        initializeBoard();

        players.remove(color);
        players.forEach((key, value) -> {
            path[value] = key.drawTui();
        });
        notifyObservers();
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

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

    public LevelView getLevel() {
        return level;
    }

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
