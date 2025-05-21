package view.tui.states;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.player.ColorView;
import view.miniModel.player.PlayerDataView;
import view.miniModel.spaceship.SpaceShipView;
import view.tui.input.Parser;
import view.tui.states.gameScreens.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerScreenTui implements ScreenTuiView {
    private final ArrayList<String> options = new ArrayList<>(List.of("Back"));
    private int totalLines;
    private int selected;
    private TuiStates oldState;

    private PlayerDataView playerToView;

    public PlayerScreenTui(PlayerDataView playerToView, TuiStates oldState) {
        this.playerToView = playerToView;
        totalLines = playerToView.getShip().getRowsToDraw() + 1;
        this.oldState = oldState;
    }

    public PlayerDataView getPlayerToView() {
        return playerToView;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public ScreenTuiView isViewCommand() {
        if (selected == 0) {
            return switch (oldState) {
                case NotClientTurnScreenTui -> new NotClientTurnScreenTui();
                case AbandonedStationScreenTui -> new AbandonedStationScreenTui();
                case BuildingScreenTui -> new BuildingScreenTui();
                case EpidemicScreenTui -> new EpidemicScreenTui();
                case CombatZoneScreenTui -> new CombatZoneScreenTui();
                case MeteorsSwarmScreenTui -> new MeteorsSwarmScreenTui();
                case PiratesScreenTui -> new PiratesScreenTui();
                case PlanetsScreenTui -> new PlanetsScreenTui();
                case SlaversScreenTui -> new SlaversScreenTui();
                case StarDustScreenTui -> new StarDustScreenTui();
                case OpenSpaceScreenTui -> new OpenSpaceScreenTui();
                case SmugglersScreenTui -> new SmugglersScreenTui();
                case AbandonedShipScreenTui -> new AbandonedShipScreenTui();
            };
        }
        return null;
    }

    @Override
    public void sendCommandToServer() {
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
        players.add(new PlayerDataView("Player1", ColorView.GREEN, new SpaceShipView(LevelView.SECOND)));

        PlayerScreenTui playerStateView = new PlayerScreenTui(players.getFirst(), TuiStates.BuildingScreenTui);
        playerStateView.printTui(terminal);
        playerStateView.readCommand(parser);
    }
}
