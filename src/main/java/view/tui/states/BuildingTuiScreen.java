package view.tui.states;

import org.javatuples.Pair;
import org.jline.terminal.Terminal;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.components.*;
import view.miniModel.deck.DeckView;
import view.miniModel.player.PlayerDataView;
import view.tui.input.Parser;

import java.util.ArrayList;

public class BuildingTuiScreen implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    protected final int totalLines;
    protected String message;

    private final int cols = 22;
    private PlayerDataView clientPlayer = MiniModel.getInstance().clientPlayer;

    private final Pair<DeckView[], Boolean[]> decksView;
    private int selected;

    public BuildingTuiScreen() {
        options.add("Pick tile");
        options.add("Put tile on spaceship");
        options.add("Put tile in reserve");
        options.add("Put the tile in the reserved pile");
        options.add("Rotate tile");
        if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
            options.add("Pick deck 1");
            options.add("Pick deck 2");
            options.add("Pick deck 3");
            options.add("Flip Timer");
        }
        options.add("Finish building");

        this.decksView = MiniModel.getInstance().deckViews;

        for (PlayerDataView p : MiniModel.getInstance().otherPlayers) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }

        totalLines = 1 + (MiniModel.getInstance().viewableComponents.size() / cols) * ComponentView.getRowsToDraw()
                + (MiniModel.getInstance().viewableComponents.size() % cols == 0 ? 0 : ComponentView.getRowsToDraw())
                + 1 + clientPlayer.getShip().getRowsToDraw() + 2;
    }

    @Override
    public void readCommand(Parser parser) throws Exception {
        selected = parser.getCommand(options, totalLines);
    }


    @Override
    public TuiScreenView setNewScreen() {
        if (selected >= options.size() - MiniModel.getInstance().otherPlayers.size()) {
            int i = selected - (options.size() - MiniModel.getInstance().otherPlayers.size());
            return new PlayerTuiScreen(MiniModel.getInstance().otherPlayers.get(i), this);
        }

        switch (selected) {
            case 0, 2, 3, 4:
                return this;
            case 1:
                return new RowAndColTuiScreen();
        }

        if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
            switch (selected) {
                case 5:
                    return new DeckTuiScreen(decksView.getValue0()[0]);
                case 6:
                    return new DeckTuiScreen(decksView.getValue0()[1]);
                case 7:
                    return new DeckTuiScreen(decksView.getValue0()[2]);
                case 8:
                    return this;
            }
        }
        return this;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        writer.print("\033[H\033[2J");
        writer.flush();

        drawTiles(writer, MiniModel.getInstance().viewableComponents);
        writer.println();

        int deckCount = 0;
        for (int i = 0; i < clientPlayer.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(clientPlayer.getShip().drawLineTui(i));
            if (i == 0) {
                line.append("Reserved pile: ");
            }
            else if (i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) - 1) {
                line.append(clientPlayer.getShip().getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) - 1) {
                line.append("     " + "   Hand: ");
            } else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) && i < ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 3 + 1)) {
                line.append("       ").append(clientPlayer.getHand().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            } else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1)) {
                line.append("              ");
            }
            if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
                if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5)) {
                    line.append("       ");
                    for (int j = 0; j < 3; j++) {
                        line.append("        ").append("Deck ").append(j + 1).append(":").append("        ").append("   ");
                    }
                }
                if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) && i < DeckView.getRowsToDraw() + ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1)) {
                    line.append("    ");
                    for (int j = 0; j < 3; j++) {
                        if (decksView.getValue1()[j]) {
                            decksView.getValue0()[j].setCovered(true);
                            line.append("   ").append(decksView.getValue0()[j].drawLineTui(deckCount));
                        } else {
                            line.append("                            ");
                        }
                    }
                    deckCount++;
                }
            }
            writer.println(line);
        }

        writer.println(message == null ? "" : message);
        writer.println();
        writer.println("Commands: ");
        writer.flush();

    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    private void drawTiles(java.io.PrintWriter writer, ArrayList<ComponentView> tiles) {
        writer.print("   ");
        for (int i = 0; i < cols; i++) {
            writer.print("  " + ((i + 1) / 10 == 0 ? " " + (i + 1) : (i + 1)) + "   ");
        }
        writer.println();
        for (int h = 0; h < tiles.size() / cols; h++) {
            for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
                if (i == 1) {
                    writer.print(((h + 1) / 10 == 0 ? ((h + 1) + "  ") : (h + 1) + " "));
                } else {
                    writer.print("   ");
                }
                for (int k = 0; k < cols; k++) {
                    writer.print(tiles.get(h * cols + k).drawLineTui(i));
                }
                writer.println();
            }
        }

        for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
            if (i == 1) {
                writer.print(((tiles.size() / cols + 1) / 10 == 0 ? ((tiles.size() / cols + 1) + "  ") : ((tiles.size() / cols + 1) + " ")));
            } else {
                writer.print("   ");
            }
            for (int k = 0; k < tiles.size() % cols; k++) {
                writer.print(tiles.get((tiles.size() / cols) * cols + k).drawLineTui(i));
            }
            writer.println();
        }
    }
}
