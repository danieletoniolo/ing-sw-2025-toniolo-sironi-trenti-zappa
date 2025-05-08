package view.tui.game;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;

enum BoardCommands {
    VIEW,
    HELP;

    public static BoardCommands from(String name) {
        try {
            return BoardCommands.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

public class BoardView {
    public void drawBoard(Board board) {
        int rows = (board.getStepsForALap()/4)*3 - 2;
        int cols = (board.getStepsForALap()/2 - (board.getStepsForALap()/4) + 2) * 7 - 2;
        int up = 0;
        int down = board.getStepsForALap() - 1;
        String[] steps = new String[board.getStepsForALap()];

        PlayerData player1 = new PlayerData("d290f1ee-6c54-4b01-90e6-d701748f0851", PlayerColor.BLUE, null);
        PlayerData player2 = new PlayerData("d290f1ee-6c54-4b01-90e6-d701748f0852", PlayerColor.RED, null);
        PlayerData player3 = new PlayerData("d290f1ee-6c54-4b01-90e6-d701748f0853", PlayerColor.GREEN, null);
        PlayerData player4 = new PlayerData("d290f1ee-6c54-4b01-90e6-d701748f0854", PlayerColor.YELLOW, null);

        board.clearInGamePlayers();
        board.setPlayer(player1, 0);
        board.setPlayer(player2, 1);
        board.setPlayer(player3, 2);
        board.setPlayer(player4, 3);

        board.addSteps(player4, 30);

        for (int i = 0; i < board.getStepsForALap(); i++) {
            switch (board.getBoardLevel()) {
                case LEARNING:
                    switch (i) {
                        case 0 -> steps[i] = "4";
                        case 1 -> steps[i] = "3";
                        case 2 -> steps[i] = "2";
                        case 4 -> steps[i] = "1";
                        default -> steps[i] = " ";
                    }
                    break;
                case SECOND:
                    switch (i) {
                        case 0 -> steps[i] = "4";
                        case 1 -> steps[i] = "3";
                        case 3 -> steps[i] = "2";
                        case 6 -> steps[i] = "1";
                        default -> steps[i] = " ";
                    }
                    break;
            }
        }
        board.refreshInGamePlayers();
        board.getInGamePlayers().forEach(player -> {
            steps[player.getStep() % board.getStepsForALap()] = getPlayerColor(player);
        });

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (i % 3) {
                    case 0:
                        if (i == 0) {
                            switch (j % 7) {
                                case 0:
                                    System.out.print("[ " + steps[up] + " ]");
                                    up++;
                                    break;
                                case 5:
                                    System.out.print("->");
                                    break;
                            }
                        } else if (i == rows - 1) {
                            switch (j % 7) {
                                case 0:
                                    System.out.print("[ " + steps[down] + " ]");
                                    down--;
                                    break;
                                case 5:
                                    System.out.print("<-");
                                    break;
                            }
                        } else {
                            if (j == cols - 5) {
                                System.out.print("[ " + steps[up] + " ]");
                                up++;
                            } else if (j == 0) {
                                System.out.print("[ " + steps[down] + " ]");
                                down--;
                            } else {
                                if (j > 4 && j < cols - 5) {
                                    System.out.print(" ");
                                }
                            }
                        }
                        break;
                    case 1:
                        if (j == 2) {
                            System.out.print("^");
                        } else if (j == cols - 3) {
                            System.out.print("|");
                        } else {
                            System.out.print(" ");
                        }
                        break;
                    case 2:
                        if (j == 2) {
                            System.out.print("|");
                        } else if (j == cols - 3) {
                            System.out.print("v");
                        } else {
                            System.out.print(" ");
                        }
                        break;
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("InGame players:");
        board.refreshInGamePlayers();
        board.getInGamePlayers().forEach(player -> {
            System.out.print("- " + player.getUsername() + ": " + (player.getStep() % board.getStepsForALap()) + " step with color " + player.getColor().toString() + "\n");
        });

        System.out.println();
        System.out.println("GaveUp players:");
        board.getGaveUpPlayers().forEach(player -> {
            System.out.print((player.getUsername() + 1) + " with color " + player.getColor().toString() + "\n");
        });

        System.out.println();
    }

    private String getPlayerColor(PlayerData player) {
        return switch (player.getColor()) {
            case BLUE -> "B";
            case RED -> "R";
            case YELLOW -> "Y";
            case GREEN -> "G";
        };
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
