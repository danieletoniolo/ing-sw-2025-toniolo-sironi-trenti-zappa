package it.polimi.ingsw.view.miniModel.board;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Map;

public class BoardView implements Structure {
    private String[] path;
    private final Map<MarkerView, Integer> players;
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
     If boolean[i] == true the deck[i] is not taken by a player, else deck is taken and not viewable in the building screen*/
    private Pair<DeckView[], Boolean[]> decksView;


    public BoardView(LevelView level) {
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
        initializeBoard();
    }

    public int getStepsForALap() {
        return stepsForALap;
    }

    public TimerView getTimerView() {
        return timerView;
    }

    public Pair<DeckView[], Boolean[]> getDecksView() {
        return decksView;
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
            path[value % stepsForALap] = key.drawTui();
        });
    }

    public void removePlayer(MarkerView color) {
        players.remove(color);
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
                int down = stepsForALap - 4;
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
