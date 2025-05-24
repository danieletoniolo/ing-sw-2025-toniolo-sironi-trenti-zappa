package view.miniModel.board;

import view.miniModel.Structure;
import view.miniModel.player.MarkerView;

import java.util.HashMap;
import java.util.Map;

public class BoardView implements Structure {
    private String[] path;
    private Map<MarkerView, Integer> players;
    private LevelView level;
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

    public BoardView(LevelView level) {
        this.level = level;
        switch (level) {
            case LEARNING -> this.stepsForALap = 18;
            case SECOND -> this.stepsForALap = 24;
        }
        this.players = new HashMap<MarkerView, Integer>();
        initializeBoard();
    }

    @Override
    public void drawGui(){
        //TODO: Implements board gui
    }

    public int getRowsToDraw() {
        return level == LevelView.LEARNING ? 9 : 13;
    }

    public int getColsToDraw() {
        return (stepsForALap / 4 + 1) * 7 + 12;
    }

    public void addPlayer(MarkerView color, int step) {
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

    public static void main(String[] args) {
        LevelView level = LevelView.SECOND;
        BoardView board = new BoardView(level);
        board.addPlayer(MarkerView.BLUE, 3);
        board.addPlayer(MarkerView.RED, 7);
        board.addPlayer(MarkerView.RED, 10);

        for (int i = 0; i < board.getRowsToDraw(); i++) {
            System.out.println(board.drawLineTui(i));
        }
    }
}
