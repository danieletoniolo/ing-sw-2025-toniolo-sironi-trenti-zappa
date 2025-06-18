package it.polimi.ingsw.view.tui.screens;

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

import java.util.ArrayList;
import java.util.List;

public abstract class BuildingTuiScreen implements TuiScreenView {
    protected final ArrayList<String> options = new ArrayList<>();
    protected final PlayerDataView clientPlayer = MiniModel.getInstance().getClientPlayer();
    protected int totalLines;

    protected String message;

    protected final Pair<DeckView[], Boolean[]> decksView;
    private final TimerView timerView = MiniModel.getInstance().getTimerView();
    protected int selected;

    public BuildingTuiScreen(List<String> otherOptions) {
        this.decksView = MiniModel.getInstance().getDeckViews();

        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
        options.add("Close program");

        totalLines = MiniModel.getInstance().getViewablePile().getRowsToDraw()
                + 1 + clientPlayer.getShip().getRowsToDraw() + 3 + 2;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
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

        return null;
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Building;
    }

    @Override
    public void printTui(Terminal terminal) {
        var writer = terminal.writer();
        int row = 1;

        ViewablePileView tiles = MiniModel.getInstance().getViewablePile();
        for (int i = 0; i < tiles.getRowsToDraw(); i++) {
            TerminalUtils.printLine(writer, tiles.drawLineTui(i), row++);
        }
        TerminalUtils.printLine(writer, "", row++);

        int deckCount = 0;
        for (int i = 0; i < clientPlayer.getShip().getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(clientPlayer.getShip().drawLineTui(i));
            if (i == 0) {
                line.append("Reserved pile: ").append(clientPlayer.getShip().getDiscardReservedPile().getReserved().size());
            }
            else if (i <= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 + 1) - 1) {
                line.append(" ").append(clientPlayer.getShip().getDiscardReservedPile().drawLineTui((i - 1) % ComponentView.getRowsToDraw()));
            }
            else if (i == ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) - 1) {
                line.append("     " + "   Hand: ");
            }
            else if (i >= ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 2 + 1) && i < ((clientPlayer.getShip().getRowsToDraw() - 2) / 5 * 3 + 1)) {
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
                    line.append("     ");
                    int hiddenTiles = MiniModel.getInstance().getNumberViewableComponents();
                    int fraction = hiddenTiles / 10;
                    line.append(timerView.drawLineTui(0));
                    line.append(" ".repeat(Math.max(0, decksView.getValue0()[0].getColsToDraw())));
                    line.append("            ").append("Hidden tiles: ").append(hiddenTiles).append(fraction >= 10 ? "" : fraction > 0 ? " " : "  ");
                }
                if (i == 1) {
                    line.append("      ");
                    //line.append(" ".repeat(Math.max(0, decksView.getValue0()[0].getColsToDraw())));
                    //line.append("    ");
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
                            line.append("   ").append(decksView.getValue0()[j].drawLineTui(deckCount));
                        } else {
                            line.append("                          ");
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
        TerminalUtils.printLine(writer, lineBeforeInput(), row);

        TerminalUtils.clearLastLines(totalLines + options.size(), terminal);
    }

    protected String lineBeforeInput(){
        return "Commands:";
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {

    }
}
