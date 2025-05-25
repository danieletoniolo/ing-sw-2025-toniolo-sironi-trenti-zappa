package view.tui.states;

import org.javatuples.Pair;
import org.jline.terminal.Terminal;
import view.miniModel.MiniModel;
import view.miniModel.board.LevelView;
import view.miniModel.components.*;
import view.miniModel.deck.DeckView;
import view.miniModel.player.PlayerDataView;
import view.miniModel.timer.TimerView;
import view.tui.TerminalUtils;
import view.tui.input.Parser;
import view.tui.states.buildingScreens.RowAndColTuiScreen;

import java.util.ArrayList;
import java.util.function.Supplier;

public class BuildingTuiScreen implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    private final int cols = 22;
    private PlayerDataView clientPlayer = MiniModel.getInstance().clientPlayer;

    protected int totalLines = 1 + (MiniModel.getInstance().viewableComponents.size() / cols) * ComponentView.getRowsToDraw()
            + (MiniModel.getInstance().viewableComponents.size() % cols == 0 ? 0 : ComponentView.getRowsToDraw())
            + 1 + clientPlayer.getShip().getRowsToDraw() + 3 + 2;

    private int row;
    protected String message;
    protected boolean isNewScreen;

    private final Pair<DeckView[], Boolean[]> decksView;
    private TimerView timerView = MiniModel.getInstance().timerView;
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
        isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
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
                    return new DeckTuiScreen(decksView.getValue0()[0], 1);
                case 6:
                    return new DeckTuiScreen(decksView.getValue0()[1], 2);
                case 7:
                    return new DeckTuiScreen(decksView.getValue0()[2], 3);
                case 8:
                    return this;
            }
        }
        return this;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Building;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        row = 1;

        drawTiles(writer, MiniModel.getInstance().viewableComponents);
        TerminalUtils.printLine(writer, "", row++);

        int deckCount = 0;
        int playerCount = 0;
        for (int i = 0; i < clientPlayer.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(clientPlayer.getShip().drawLineTui(i));
            if (i == 0) {
                line.append("Reserved pile:");
            }
            else if (i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) - 1) {
                line.append(clientPlayer.getShip().getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) - 1) {
                line.append("     " + "   Hand: ");
            } else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) && i < ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 3 + 1)) {
                line.append("       ").append(clientPlayer.getHand().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i > ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 4 + 1) - 1 && i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 4 + clientPlayer.getRowsToDraw())) {
                line.append("   ").append(clientPlayer.drawLineTui(playerCount));
                if (playerCount == 0) {
                    line.append("    ");
                }
                playerCount++;
            }
            else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1)) {
                line.append("              ");
            }
            if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
                if (i == 0) {
                    line.append("       ");
                    line.append(" ".repeat(Math.max(0, decksView.getValue0()[0].getColsToDraw())));
                    line.append("   ");
                    line.append(timerView.drawLineTui(0));
                }
                if (i == 1) {
                    line.append("       ");
                    line.append(" ".repeat(Math.max(0, decksView.getValue0()[0].getColsToDraw())));
                    line.append("   ");
                    line.append(timerView.drawLineTui(1));
                }
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
            TerminalUtils.printLine(writer, line.toString(), row++);
        }
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, message == null ? "" : message, row++);
        TerminalUtils.printLine(writer, "", row++);
        TerminalUtils.printLine(writer, "Commands: ", row++);

        if (isNewScreen) {
            isNewScreen = false;
            for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
                TerminalUtils.printLine(writer, "", i);
            }
        }
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    private void drawTiles(java.io.PrintWriter writer, ArrayList<ComponentView> tiles) {
        StringBuilder line = new StringBuilder();
        line.append("   ");
        for (int i = 0; i < cols; i++) {
            line.append("  ").append((i + 1) / 10 == 0 ? " " + (i + 1) : (i + 1)).append("   ");
        }
        TerminalUtils.printLine(writer, line.toString(), row++);
        line.setLength(0);
        for (int h = 0; h < tiles.size() / cols; h++) {
            for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
                if (i == 1) {
                    line.append(((h + 1) / 10 == 0 ? ((h + 1) + "  ") : (h + 1) + " "));
                } else {
                    line.append("   ");
                }
                for (int k = 0; k < cols; k++) {
                    line.append(tiles.get(h * cols + k).drawLineTui(i));
                }
                TerminalUtils.printLine(writer, line.toString(), row++);
                line.setLength(0);
            }
        }

        line.setLength(0);
        for (int i = 0; i < ComponentView.getRowsToDraw(); i++) {
            if (i == 1) {
                line.append(((tiles.size() / cols + 1) / 10 == 0 ? ((tiles.size() / cols + 1) + "  ") : ((tiles.size() / cols + 1) + " ")));
            } else {
                line.append("   ");
            }
            for (int k = 0; k < tiles.size() % cols; k++) {
                line.append(tiles.get((tiles.size() / cols) * cols + k).drawLineTui(i));
            }
            TerminalUtils.printLine(writer, line.toString(), row++);
            line.setLength(0);
        }
    }
}
