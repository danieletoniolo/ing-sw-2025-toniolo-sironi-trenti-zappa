package it.polimi.ingsw.view.tui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;
import it.polimi.ingsw.view.tui.TerminalUtils;
import it.polimi.ingsw.view.tui.input.Parser;
import org.javatuples.Pair;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;

public abstract class ValidationTuiScreen implements TuiScreenView {
    protected ArrayList<String> options = new ArrayList<>();
    private boolean isNewScreen;

    protected int totalLines;
    protected int selected;
    protected TuiScreenView nextState;

    protected static SpaceShipView spaceShipView;
    protected static List<Pair<Integer, Integer>> destroyTiles;
    private String message;

    public ValidationTuiScreen(List<String> otherOptions) {
        if (spaceShipView == null) {
            spaceShipView = MiniModel.getInstance().getClientPlayer().getShip().clone();
        }
        if (destroyTiles == null) {
            destroyTiles = new ArrayList<>();
        }

        if (otherOptions != null && !otherOptions.isEmpty()) options.addAll(otherOptions);

        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.drawLineTui(0) + "'s spaceship");
        }
        options.add("Close program");

        totalLines = spaceShipView.getRowsToDraw() + 5;
        this.isNewScreen = true;
    }

    @Override
    public void readCommand(Parser parser) {
        selected = parser.getCommand(options, totalLines);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Validation;
    }

    @Override
    public TuiScreenView setNewScreen() {
        if ((selected < options.size() - 1) && (selected >= options.size() - 1 - MiniModel.getInstance().getOtherPlayers().size())) {
            int i = selected - (options.size() - MiniModel.getInstance().getOtherPlayers().size() - 1);

            return new OtherPlayer(MiniModel.getInstance().getOtherPlayers().get(i), this);
        }

        if (selected == options.size() - 1) {
            return new ClosingProgram();
        }

        if (selected < 0 || selected >= options.size()) {
            return this;
        }

        return null;
    }

    @Override
    public synchronized void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void printTui(Terminal terminal) {
        List<String> newLines = new ArrayList<>();

        int playerCount = 0;
        for (int i = 0; i < spaceShipView.getRowsToDraw(); i++) {
            StringBuilder line = new StringBuilder();
            line.append(spaceShipView.drawLineTui(i));

            if (i <= spaceShipView.getDiscardReservedPile().getRowsToDraw()) {
                line.append(MiniModel.getInstance().getClientPlayer().getShip().getDiscardReservedPile().drawLineTui(i));
            }
            else if (i > ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + 1) - 1 && i <= ((spaceShipView.getRowsToDraw() - 2) / 5 * 4 + MiniModel.getInstance().getClientPlayer().getRowsToDraw())) {
                line.append("   ").append(MiniModel.getInstance().getClientPlayer().drawLineTui(playerCount));
                if (playerCount == 0) {
                    line.append("    ");
                }
                playerCount++;
            }
            newLines.add(line.toString());
        }

        newLines.add("");
        newLines.add(message == null ? "" : message);
        newLines.add("");
        newLines.add(lineBeforeInput());

        TerminalUtils.printScreen(newLines, totalLines + options.size());

        if (isNewScreen) {
            isNewScreen = false;
            TerminalUtils.clearLastLines(totalLines + options.size());
        }
    }

    protected String lineBeforeInput() {
        return "Commands:";
    }

    @Override
    public void setNextScreen(TuiScreenView nextScreen) {
        this.nextState = nextScreen;
    }
}
