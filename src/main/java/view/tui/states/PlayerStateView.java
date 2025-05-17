package view.tui.states;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.structures.MiniModel;
import view.structures.board.LevelView;
import view.structures.player.ColorView;
import view.structures.player.PlayerDataView;
import view.structures.spaceship.SpaceShipView;
import view.tui.input.Command;
import view.tui.input.Parser;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerStateView implements StateView{
    private ArrayList<String> options = new ArrayList<>(Arrays.asList("Back"));
    private PlayerDataView player;
    private int totalLines = PlayerDataView.getRowsToDraw() + 2 /*Lines added in the state print*/ + SpaceShipView.getRowToDraw();

    public PlayerStateView(String userID) {
        this.player = MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(userID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getTotalLines() {
        return totalLines;
    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public StateView internalViewState(Command command) {
        return null; // Placeholder for the next state
    }

    @Override
    public void printTui(org.jline.terminal.Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        for (int i = 0; i < PlayerDataView.getRowsToDraw(); i++) {
            writer.println(player.drawLineTui(i));
        }
        writer.println();
        writer.flush();
        for (int i = 0; i < SpaceShipView.getRowToDraw(); i++) {
            writer.println(player.getShip().drawLineTui(i));
        }

        writer.print("\nOptions:");
        writer.flush();
    }

    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
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


        ArrayList<PlayerDataView> players = MiniModel.getInstance().players;
        players.add(new PlayerDataView("Player1", ColorView.RED, new SpaceShipView(LevelView.SECOND)));

        PlayerStateView playerStateView = new PlayerStateView("Player1");
        playerStateView.printTui(terminal);

        parser.getCommand(playerStateView.getOptions(), playerStateView.getTotalLines());
    }
}
