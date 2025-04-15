package view.TUI.game;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;

import java.util.HashMap;
import java.util.Map;

public class BoardView {
    public void drawBoard(Board board) {
        int rows = board.getStepsForALap() / 4;
        int cols = board.getStepsForALap() / 2 - rows + 2;
        int up = 1;
        int down = board.getStepsForALap();
        Map<Integer, PlayerData> positions = new HashMap<>();

        board.setPlayer(new PlayerData("pino", PlayerColor.BLUE, null), 0);
        //board.setPlayer(new PlayerData("gino", PlayerColor.RED, null), 1);
        //board.setPlayer(new PlayerData("vino", PlayerColor.YELLOW, null), 3);


        board.updateInGamePlayers().forEach(player -> {
            positions.put(player.getPosition(), player);
        });

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || j == cols - 1) {
                    System.out.print("[ " + (positions.containsKey(up) ? positions.get(up).getColor().toString() : (up/10 == 0 ? " " + up : up)) + " ]");
                    up++;
                }
                else if (i == rows - 1 || j == 0) {
                    System.out.print("[ " + (down/10 == 0 ? " " + down : down) + " ]");
                    down--;
                }
                else {
                    System.out.print("      ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        try {
            Board board = new Board(Level.SECOND);
            BoardView boardView = new BoardView();
            boardView.drawBoard(board);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
