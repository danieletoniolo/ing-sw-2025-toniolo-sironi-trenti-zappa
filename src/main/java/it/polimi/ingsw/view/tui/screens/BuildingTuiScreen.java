package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.event.game.clientToServer.deck.PickLeaveDeck;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToReserve;
import it.polimi.ingsw.event.game.clientToServer.rotateTile.RotateTile;
import it.polimi.ingsw.event.game.clientToServer.timer.FlipTimer;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.tui.screens.buildingScreens.ChoosePositionTuiScreen;
import org.javatuples.Pair;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.components.*;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import it.polimi.ingsw.view.tui.screens.buildingScreens.RowAndColTuiScreen;

import java.util.ArrayList;
import java.util.function.Supplier;

public class BuildingTuiScreen implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    private final int cols = 22;
    private final PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();

    protected int totalLines = 1 + (MiniModel.getInstance().getViewableComponents().size() / cols) * ComponentView.getRowsToDraw()
            + (MiniModel.getInstance().getViewableComponents().size() % cols == 0 ? 0 : ComponentView.getRowsToDraw())
            + 1 + clientPlayer.getShip().getRowsToDraw() + 3 + 2;

    private int row;
    protected String message;

    private final Pair<DeckView[], Boolean[]> decksView;
    private final TimerView timerView = MiniModel.getInstance().getTimerView();
    private int selected;

    public BuildingTuiScreen() {
        options.add("Pick an hidden tile");
        options.add("Pick a tile from the board");
        options.add("Put tile on spaceship");
        options.add("Put the tile in the reserved pile");
        options.add("Rotate tile");
        if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
            options.add("Pick deck 1");
            options.add("Pick deck 2");
            options.add("Pick deck 3");
            options.add("Flip Timer");
        }
        options.add("Finish building");

        this.decksView = MiniModel.getInstance().getDeckViews();

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");
    }

    @Override
    public void readCommand(Parser parser, Supplier<Boolean> isStillCurrentScreen) throws Exception {
        selected = parser.getCommand(options, totalLines, isStillCurrentScreen);
    }


    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);
            return new PlayerTuiScreen(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        StatusEvent status;

        if (selected == 0) {
            status = PickTileFromBoard.requester(Client.transceiver, new Object()).request(new PickTileFromBoard(MiniModel.getInstance().getUserID(), -1));
            if (status.get().equals("POTA")) {
                setMessage("Problem Occurred, Try Again!");
                return this;
            }
            return this;
        }

        if (selected == 1) {
            return new RowAndColTuiScreen(TuiScreens.RowColBoard);
        }

        if (selected == 2) {
            return new RowAndColTuiScreen(TuiScreens.RowColShip);
        }

        if (selected == 3) {
            status = PlaceTileToReserve.requester(Client.transceiver, new Object()).request(new PlaceTileToReserve(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage("Reserved pile is full");
            }
            return this;
        }

        if (selected == 4) {
            status = RotateTile.requester(Client.transceiver, new Object())
                    .request(new RotateTile(MiniModel.getInstance().getUserID(), clientPlayer.getHand().getID()));
            if (status.get().equals("POTA")) {
                setMessage("Rotate tile failed. Try again!");
            }
            return this;
        }

        if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
            switch (selected) {
                case 5:
                    status = PickLeaveDeck.requester(Client.transceiver, new Object())
                            .request(new PickLeaveDeck(MiniModel.getInstance().getUserID(), 0, 0));
                    if (status.get().equals("POTA")) {
                        setMessage("It is not possible to pick Deck 1");
                        return this;
                    }

                    return new DeckTuiScreen(decksView.getValue0()[0], 1);
                case 6:
                    status = PickLeaveDeck.requester(Client.transceiver, new Object())
                            .request(new PickLeaveDeck(MiniModel.getInstance().getUserID(), 0, 1));
                    if (status.get().equals("POTA")) {
                        setMessage("It is not possible to pick Deck 2");
                        return this;
                    }
                    return new DeckTuiScreen(decksView.getValue0()[1], 2);
                case 7:
                    status = PickLeaveDeck.requester(Client.transceiver, new Object())
                            .request(new PickLeaveDeck(MiniModel.getInstance().getUserID(), 0, 2));
                    if (status.get().equals("POTA")) {
                        setMessage("It is not possible to pick Deck 3");
                        return this;
                    }
                    return new DeckTuiScreen(decksView.getValue0()[2], 3);
                case 8:
                    status = FlipTimer.requester(Client.transceiver, new Object()).request(new FlipTimer(MiniModel.getInstance().getUserID()));
                    if (status.get().equals("POTA")) {
                        setMessage("Timer's active - can’t flip it!");
                    }
                    return this;
            }
        }

        /*//status = FinishBuild;
        if (status.get().equals("POTA")) {
            setMessage("Hold on - the timer is still running!");
            return this;
        }
        if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
            status = FlipTimer.requester(Client.transceiver, new Object()).request(new FlipTimer(MiniModel.getInstance().getUserID()));
            if (status.get().equals("POTA")) {
                setMessage("Timer's active - can’t flip it!");
            }
        }*/
        return new ChoosePositionTuiScreen();
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Building;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        row = 1;

        drawTiles(writer, MiniModel.getInstance().getViewableComponents());
        TerminalUtils.printLine(writer, "", row++);

        int deckCount = 0;
        for (int i = 0; i < clientPlayer.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(clientPlayer.getShip().drawLineTui(i));
            if (i == 0) {
                int hiddenTiles = 152 - MiniModel.getInstance().getViewableComponents().size();
                int fraction = hiddenTiles / 10;
                line.append("Reserved pile:").append("        ").append("Hidden tiles: ").append(hiddenTiles).append(fraction >= 10 ? "" : fraction > 0 ? " " : "  ");
            }
            else if (i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) - 1) {
                line.append(clientPlayer.getShip().getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) - 1) {
                line.append("     " + "   Hand: ");
            } else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) && i < ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 3 + 1)) {
                line.append("       ").append(clientPlayer.getHand().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i == clientPlayer.getShip().getRowsToDraw() - 1) {
                line.append("   ").append(clientPlayer.drawLineTui(0));
            }
            else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1)) {
                line.append("              ");
            }
            if (clientPlayer.getShip().getLevel().equals(LevelView.SECOND)) {
                if (i == 0) {
                    line.append("         ");
                    line.append(timerView.drawLineTui(0));
                }
                if (i == 1) {
                    line.append("       ");
                    line.append(" ".repeat(Math.max(0, decksView.getValue0()[0].getColsToDraw())));
                    line.append("    ");
                    line.append(timerView.drawLineTui(1));
                }
                if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5) + 1) {
                    line.append("       ");
                    for (int j = 0; j < 3; j++) {
                        line.append("        ").append("Deck ").append(j + 1).append(":").append("        ").append("   ");
                    }
                }
                if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) + 1 && i < DeckView.getRowsToDraw() + ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) + 1) {
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
        TerminalUtils.printLine(writer, lineBeforeInput(), row++);

        for (int i = totalLines + options.size(); i < terminal.getSize().getRows(); i++ ) {
            TerminalUtils.printLine(writer, "", i);
        }
    }

    protected String lineBeforeInput(){
        return "Commands:";
    }

    @Override
    public synchronized void setMessage(String message) {
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
