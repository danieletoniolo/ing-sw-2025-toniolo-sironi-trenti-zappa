package view.structures.board;

import view.structures.player.ColorView;

import java.util.HashMap;
import java.util.Map;

public class BoardView {
    private String[] path;
    private Map<ColorView, Integer> players;
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

    public BoardView(LevelView level, int stepsForALap) {
        this.level = level;
        this.stepsForALap = stepsForALap;
        this.players = new HashMap<ColorView, Integer>();
        initializeBoard();
    }

    public void drawBoardGui(){
        //TODO: Implements board gui
    }

    public int getRowsToDraw() {
        return level == LevelView.LEARNING ? 9 : 13;
    }

    public void addPlayer(ColorView color, int step) {
        initializeBoard();
        players.put(color, step);
        players.forEach((key, value) -> {
            path[value % stepsForALap] = key.drawTui();
        });
    }

    public void removePlayer(ColorView color) {
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
                print.append(Dash).append(Dash).append(Dash).append(ArrowRight).append(Bow2).append(" ");
                return print.toString();
            }
            if (line == getRowsToDraw() - 1) {
                int down = stepsForALap - 4;
                print.append("  ").append(Bow3).append(ArrowLeft).append(Dash).append(Dash).append(Dash);
                for (int i = 0; i < cols; i++) {
                    print.append("[ ").append(path[down]).append(" ]").append(ArrowLeft).append(Dash);
                    down--;
                }
                print.append(Dash).append(Dash).append(Bow4).append(" ");
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
        int steps = level == LevelView.LEARNING ? 18 : 24;
        BoardView board = new BoardView(level, steps);
        board.addPlayer(ColorView.BLUE, 4);
        board.addPlayer(ColorView.RED, 7);
        board.addPlayer(ColorView.RED, 10);

        for (int i = 0; i < board.getRowsToDraw(); i++) {
            System.out.println(board.drawLineTui(i));
        }
    }
}
