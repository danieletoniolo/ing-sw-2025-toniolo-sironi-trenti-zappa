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

public class PlayerStateTuiView implements StateTuiView {
    private ArrayList<String> options = new ArrayList<>(Arrays.asList("Back"));
    private PlayerDataView player;

    public PlayerStateTuiView() {
        this.player = MiniModel.getInstance().players.stream()
                .filter(player -> player.getUsername().equals(MiniModel.getInstance().playerToView))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getTotalLines() {
        return player.getShip().getRowsToDraw() + 1;
    }

    @Override
    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public StateTuiView internalViewState(Command command) {
        if (command.name().equals(options.getFirst())) {
            return null; //new GameStateView();// Placeholder for the next state
        }
        return null;
    }

    @Override
    public void printTui(org.jline.terminal.Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();
        for (int i = 0; i < player.getShip().getRowsToDraw(); i++) {
            if (i >= ((player.getShip().getRowsToDraw() - 2)/5*4 + 1) - 1 && i < ((player.getShip().getRowsToDraw() - 2)/5*4 + 1) - 1 + player.getRowsToDraw()) {
                writer.println(player.getShip().drawLineTui(i) + "   " + player.drawLineTui(i % player.getRowsToDraw()));
            }else{
                writer.println(player.getShip().drawLineTui(i));
            }
        }

        writer.print("\nOptions:");
        writer.flush();
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
        players.add(new PlayerDataView("Player1", ColorView.GREEN, new SpaceShipView(LevelView.SECOND)));

        MiniModel.getInstance().playerToView = "Player1";
        PlayerStateTuiView playerStateView = new PlayerStateTuiView();
        playerStateView.printTui(terminal);

        parser.getCommand(playerStateView.getOptions(), playerStateView.getTotalLines());
    }
}
