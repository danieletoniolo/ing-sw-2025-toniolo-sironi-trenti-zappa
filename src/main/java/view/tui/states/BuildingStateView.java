package view.tui.states;

import Model.SpaceShip.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import view.structures.MiniModel;
import view.structures.board.LevelView;
import view.structures.components.*;
import view.structures.player.ColorView;
import view.structures.player.PlayerDataView;
import view.structures.spaceship.SpaceShipView;
import view.tui.input.Command;
import view.tui.input.Parser;

import java.util.ArrayList;

public class BuildingStateView implements StateView {
    private final ArrayList<String> options = new ArrayList<>();
    private int totalLines = 1 + (MiniModel.getInstance().components.size() / 13) * ComponentView.getRowsToDraw() + (MiniModel.getInstance().components.size() % 13 == 0 ? 0 : ComponentView.getRowsToDraw()) + 2 + SpaceShipView.getRowToDraw();
    private PlayerDataView player;

    public BuildingStateView(String userID) {
        options.add("Pick tile ('row, column')");
        options.add("Put tile on spaceship");
        options.add("Put tile in reserve");
        options.add("Put tile in the pile");
        for (PlayerDataView player : MiniModel.getInstance().players) {
            options.add("View " + player.getUsername() + "'s spaceship");
        }

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
        if (command.name().equals("Pick")) {


            return this;
        }

        return null; // Placeholder for the next state
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        drawTiles(writer, MiniModel.getInstance().components, 13);
        writer.println();
        writer.println();


        for (int i = 0; i < SpaceShipView.getRowToDraw(); i++) {
            if (i == 10) {
                writer.println(player.getShip().drawLineTui(i) + (player.getHand() != null ? ("        " + "    Hand:    ") : ""));
            }
            else if(i >= 11 && i <= 15) {
                writer.println(player.getShip().drawLineTui(i) + "        " + player.getHand().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }else{
                writer.println(player.getShip().drawLineTui(i));
            }
        }
        writer.println("Commands:");
        writer.flush();

    }

    private void drawTiles(java.io.PrintWriter writer, ArrayList<ComponentView> tiles, int cols) {
        writer.print("   ");
        for (int i = 0; i < cols; i++) {
            writer.print("      " + (i+1) + "      ");
        }
        writer.println();
        for (int h = 0; h < tiles.size() / cols; h++) {
            for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
                if (i == 2) {
                    writer.print(" " + ((h+1) / 10 == 0 ? ((h+1) + "  ") : (h+1) + " "));
                }else{
                    writer.print("    ");
                }
                for (int k = 0; k < cols; k++) {
                    writer.print(tiles.get(h * cols + k).drawLineTui(i));
                }
                writer.println();
            }
        }

        for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
            if (i == 2) {
                writer.print(" " + ((tiles.size()/cols+1) / 10 == 0 ? ((tiles.size()/cols+1) + "  ") : ((tiles.size()/cols+1) + " ")));
            }else{
                writer.print("    ");
            }
            for (int k = 0; k < tiles.size() % cols; k++) {
                writer.print(tiles.get((tiles.size() / cols) * cols + k).drawLineTui(i));
            }
            writer.println();
        }
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

        ArrayList<Component> tiles = TilesManager.getTiles();
        for (Component tile : tiles) {
            MiniModel.getInstance().components.add(converter(tile));
        }

        ArrayList<PlayerDataView> players = MiniModel.getInstance().players;
        players.add(new PlayerDataView("Player1", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player2", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player3", ColorView.RED, new SpaceShipView(LevelView.SECOND)));
        players.add(new PlayerDataView("Player4", ColorView.RED, new SpaceShipView(LevelView.SECOND)));

        players.getFirst().setHand(MiniModel.getInstance().components.remove(56));
        players.getFirst().getHand().setCovered(false);

        BuildingStateView buildingStateView = new BuildingStateView("Player1");
        buildingStateView.printTui(terminal);
        parser.getCommand(buildingStateView.getOptions(), buildingStateView.getTotalLines());
    }

    private static ComponentView converter(Component tile) {
        int[] connectors = new int[4];
        for (int j = 0; j < 4; j++) {
            switch (tile.getConnection(j)) {
                case EMPTY -> connectors[j] = 0;
                case SINGLE -> connectors[j] = 1;
                case DOUBLE -> connectors[j] = 2;
                case TRIPLE -> connectors[j] = 3;
            }
        }

        return switch (tile.getComponentType()) {
            case BATTERY -> new BatteryView(tile.getID(), connectors, ((Battery) tile).getEnergyNumber());
            case CABIN -> new CabinView(tile.getID(), connectors);
            case STORAGE -> new StorageView(tile.getID(), connectors, ((Storage) tile).isDangerous(), ((Storage) tile).getGoodsCapacity());
            case BROWN_LIFE_SUPPORT -> new LifeSupportBrownView(tile.getID(), connectors);
            case PURPLE_LIFE_SUPPORT -> new LifeSupportPurpleView(tile.getID(), connectors);
            case SINGLE_CANNON, DOUBLE_CANNON -> new CannonView(tile.getID(), connectors, ((Cannon) tile).getCannonStrength(), tile.getClockwiseRotation());
            case SINGLE_ENGINE, DOUBLE_ENGINE -> new EngineView(tile.getID(), connectors, ((Engine) tile).getEngineStrength(), tile.getClockwiseRotation());
            case SHIELD -> {
                boolean[] shields = new boolean[4];
                for (int i = 0; i < 4; i++) shields[i] = ((Shield) tile).canShield(i);
                yield new ShieldView(tile.getID(), connectors, shields);
            }
            case CONNECTORS -> new ConnectorsView(tile.getID(), connectors);
            default -> throw new IllegalStateException("Unexpected value: " + tile.getComponentType());
        };
    }
}
