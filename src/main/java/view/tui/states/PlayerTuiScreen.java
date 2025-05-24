package view.tui.states;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.player.MarkerView;
import view.miniModel.player.PlayerDataView;
import view.miniModel.spaceship.SpaceShipView;
import view.tui.input.Parser;

import java.util.ArrayList;
import java.util.List;

public class PlayerTuiScreen implements TuiScreenView {
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));
    private int totalLines;
    private int selected;
    private TuiScreenView oldScreen;
    private String message;

    private PlayerDataView playerToView;

    public PlayerTuiScreen(PlayerDataView playerToView, TuiScreenView oldScreen) {
        this.playerToView = playerToView;
        totalLines = playerToView.getShip().getRowsToDraw() + 1;
        this.oldScreen = oldScreen;
    }

    public PlayerDataView getPlayerToView() {
        return playerToView;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreenView setNewScreen() {
        return oldScreen;
    }

    @Override
    public void printTui(org.jline.terminal.Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();
        for (int i = 0; i < playerToView.getShip().getRowsToDraw(); i++) {
            if (i >= ((playerToView.getShip().getRowsToDraw() - 2)/5*4 + 1) - 1 && i < ((playerToView.getShip().getRowsToDraw() - 2)/5*4 + 1) - 1 + playerToView.getRowsToDraw()) {
                writer.println(playerToView.getShip().drawLineTui(i) + "   " + playerToView.drawLineTui(i % playerToView.getRowsToDraw()));
            }else{
                writer.println(playerToView.getShip().drawLineTui(i));
            }
        }

        writer.print("\nCommands:");
        writer.flush();
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    public static void main(String[] args) throws Exception {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .jna(true)
                    .jansi(true)
                    .build();
        } catch (Exception e) {
            System.err.println("Creation terminal error: " + e.getMessage());
            return;
        }
        Parser parser = new Parser(terminal);


        ArrayList<PlayerDataView> players = MiniModel.getInstance().otherPlayers;
        players.add(new PlayerDataView("Player1", MarkerView.GREEN, new SpaceShipView(LevelView.SECOND)));

        PlayerTuiScreen playerStateView = new PlayerTuiScreen(players.getFirst(), new BuildingTuiScreen());
        playerStateView.printTui(terminal);
        playerStateView.readCommand(parser);
    }
}
